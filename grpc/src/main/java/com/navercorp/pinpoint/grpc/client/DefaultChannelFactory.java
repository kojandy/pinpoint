/*
 * Copyright 2024 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.grpc.client;

import com.google.common.util.concurrent.MoreExecutors;
import com.navercorp.pinpoint.common.profiler.concurrent.PinpointThreadFactory;
import com.navercorp.pinpoint.grpc.ChannelTypeEnum;
import com.navercorp.pinpoint.grpc.client.config.ClientOption;
import com.navercorp.pinpoint.grpc.client.config.ClientRetryOption;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.NameResolverProvider;
import io.grpc.netty.InternalNettyChannelBuilder;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Woonduk Kang(emeroad)
 */
public class DefaultChannelFactory implements ChannelFactory {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final String factoryName;

    private final int executorQueueSize;
    private final HeaderFactory headerFactory;

    private final ClientOption clientOption;
    // nullable
    private final SslContext sslContext;

    // nullable
    private final ClientRetryOption clientRetryOption;

    private final List<ClientInterceptor> clientInterceptorList;
    private final NameResolverProvider nameResolverProvider;

    // state object
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService eventLoopExecutor;
    private final ExecutorService executorService;
    private final Class<? extends Channel> channelType;

    DefaultChannelFactory(String factoryName,
                          int executorQueueSize,
                          HeaderFactory headerFactory,
                          NameResolverProvider nameResolverProvider,
                          ClientOption clientOption,
                          List<ClientInterceptor> clientInterceptorList,
                          SslContext sslContext,
                          ClientRetryOption clientRetryOption) {
        this.factoryName = Objects.requireNonNull(factoryName, "factoryName");
        this.executorQueueSize = executorQueueSize;
        this.headerFactory = Objects.requireNonNull(headerFactory, "headerFactory");
        // @Nullable
        this.nameResolverProvider = nameResolverProvider;
        this.clientOption = Objects.requireNonNull(clientOption, "clientOption");

        Objects.requireNonNull(clientInterceptorList, "clientInterceptorList");
        this.clientInterceptorList = new ArrayList<>(clientInterceptorList);
        // nullable
        this.sslContext = sslContext;
        // nullable
        this.clientRetryOption = clientRetryOption;


        ChannelType channelType = getChannelType();
        this.channelType = channelType.getChannelType();

        this.eventLoopExecutor = newCachedExecutorService(factoryName + "-Channel-Worker");
        this.eventLoopGroup = channelType.newEventLoopGroup(1, eventLoopExecutor);
        this.executorService = newExecutorService(factoryName + "-Channel-Executor", this.executorQueueSize);
    }

    @Override
    public String getFactoryName() {
        return factoryName;
    }

    private ChannelType getChannelType() {
        ChannelTypeFactory factory = new ChannelTypeFactory();
        ChannelTypeEnum channelTypeEnum = clientOption.getChannelTypeEnum();
        return factory.newChannelType(channelTypeEnum);
    }


    private ExecutorService newCachedExecutorService(String name) {
        ThreadFactory threadFactory = new PinpointThreadFactory(PinpointThreadFactory.DEFAULT_THREAD_NAME_PREFIX + name, true);
        return Executors.newCachedThreadPool(threadFactory);
    }

    private ExecutorService newExecutorService(String name, int executorQueueSize) {
        ThreadFactory threadFactory = new PinpointThreadFactory(PinpointThreadFactory.DEFAULT_THREAD_NAME_PREFIX + name, true);
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(executorQueueSize);
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                workQueue, threadFactory);
    }

    @Override
    public ManagedChannel build(String host, int port) {
        return build(this.factoryName, host, port);
    }

    @Override
    public ManagedChannel build(String channelName, String host, int port) {
        final NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(host, port);
        channelBuilder.usePlaintext();

        logger.info("ChannelType:{}", channelType.getSimpleName());
        channelBuilder.channelType(channelType);
        channelBuilder.eventLoopGroup(eventLoopGroup);

        setupInternal(channelBuilder);

        addHeader(channelBuilder);
        addClientInterceptor(channelBuilder);

        channelBuilder.executor(executorService);
        if (nameResolverProvider != null) {
            logger.info("Set nameResolverProvider {}. channelName={}, host={}, port={}", this.nameResolverProvider, channelName, host, port);
            setNameResolverFactory(channelBuilder, this.nameResolverProvider);
        }
        setupClientOption(channelBuilder);

        if (sslContext != null) {
            logger.info("{} enable SslContext", channelName);
            channelBuilder.sslContext(sslContext);
            channelBuilder.negotiationType(NegotiationType.TLS);
        }

        // RetryOption
        if (clientRetryOption != null) {
            setupRetryOption(channelBuilder);
        }


        channelBuilder.maxTraceEvents(clientOption.getMaxTraceEvent());

        return channelBuilder.build();
    }

    @SuppressWarnings("deprecation")
    private void setNameResolverFactory(NettyChannelBuilder channelBuilder, NameResolverProvider nameResolverProvider) {
        channelBuilder.nameResolverFactory(nameResolverProvider);
    }

    private void setupInternal(NettyChannelBuilder channelBuilder) {
        InternalNettyChannelBuilder.setTracingEnabled(channelBuilder, false);

        InternalNettyChannelBuilder.setStatsEnabled(channelBuilder, false);
        InternalNettyChannelBuilder.setStatsRecordStartedRpcs(channelBuilder, false);
        InternalNettyChannelBuilder.setStatsRecordFinishedRpcs(channelBuilder, false);
        InternalNettyChannelBuilder.setStatsRecordRealTimeMetrics(channelBuilder, false);
    }

    private void addHeader(NettyChannelBuilder channelBuilder) {
        final Metadata extraHeaders = headerFactory.newHeader();
        if (logger.isDebugEnabled()) {
            logger.debug("addHeader {}", extraHeaders);
        }
        final ClientInterceptor headersInterceptor = MetadataUtils.newAttachHeadersInterceptor(extraHeaders);
        channelBuilder.intercept(headersInterceptor);
    }

    private void addClientInterceptor(NettyChannelBuilder channelBuilder) {
        channelBuilder.intercept(clientInterceptorList);
    }

    private void setupClientOption(final NettyChannelBuilder channelBuilder) {
        channelBuilder.keepAliveTime(clientOption.getKeepAliveTime(), TimeUnit.MILLISECONDS);
        channelBuilder.keepAliveTimeout(clientOption.getKeepAliveTimeout(), TimeUnit.MILLISECONDS);
        channelBuilder.keepAliveWithoutCalls(clientOption.isKeepAliveWithoutCalls());
        channelBuilder.maxInboundMetadataSize(clientOption.getMaxHeaderListSize());
        channelBuilder.maxInboundMessageSize(clientOption.getMaxInboundMessageSize());
        channelBuilder.flowControlWindow(clientOption.getFlowControlWindow());
        channelBuilder.idleTimeout(clientOption.getIdleTimeoutMillis(), TimeUnit.MILLISECONDS);
        channelBuilder.defaultLoadBalancingPolicy(clientOption.getDefaultLoadBalancer());

        // ChannelOption
        channelBuilder.withOption(ChannelOption.TCP_NODELAY, true);
        channelBuilder.withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientOption.getConnectTimeout());

        final WriteBufferWaterMark writeBufferWaterMark = new WriteBufferWaterMark(clientOption.getWriteBufferLowWaterMark(), clientOption.getWriteBufferHighWaterMark());
        channelBuilder.withOption(ChannelOption.WRITE_BUFFER_WATER_MARK, writeBufferWaterMark);
        if (logger.isInfoEnabled()) {
            logger.info("Set clientOption {}. name={}", clientOption, factoryName);
        }
    }

    private void setupRetryOption(final NettyChannelBuilder channelBuilder) {
        channelBuilder.enableRetry();
        channelBuilder.retryBufferSize(clientRetryOption.getRetryBufferSize());
        channelBuilder.perRpcBufferLimit(clientRetryOption.getPerRpcBufferLimit());

        //channelBuilder.disableServiceConfigLookUp();
        channelBuilder.defaultServiceConfig(clientRetryOption.getRetryServiceConfig());
        if (logger.isDebugEnabled()) {
            logger.debug("Set clientRetryOption {}. name={}", clientRetryOption, factoryName);
        }
    }

    @Override
    public void close() {
        final Future<?> future = eventLoopGroup.shutdownGracefully();
        try {
            logger.debug("shutdown {}-eventLoopGroup", factoryName);
            future.await(1000 * 3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (!MoreExecutors.shutdownAndAwaitTermination(eventLoopExecutor, Duration.ofSeconds(3))) {
            logger.warn("{}-eventLoopExecutor shutdown failed", factoryName);
        }
        if (!MoreExecutors.shutdownAndAwaitTermination(executorService, Duration.ofSeconds(3))) {
            logger.warn("{}-executorService shutdown failed", factoryName);
        }
    }

    @Override
    public String toString() {
        return "DefaultChannelFactory{" + "factoryName='" + factoryName + '\'' +
                ", executorQueueSize=" + executorQueueSize +
                ", headerFactory=" + headerFactory +
                ", clientOption=" + clientOption +
                ", clientInterceptorList=" + clientInterceptorList +
                ", nameResolverProvider=" + nameResolverProvider +
                ", eventLoopGroup=" + eventLoopGroup +
                ", eventLoopExecutor=" + eventLoopExecutor +
                ", executorService=" + executorService +
                '}';
    }
}