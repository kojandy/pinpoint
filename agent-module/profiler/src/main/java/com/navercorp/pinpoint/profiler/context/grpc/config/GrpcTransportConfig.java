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

package com.navercorp.pinpoint.profiler.context.grpc.config;

import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.module.JavaModule;
import com.navercorp.pinpoint.common.config.Value;
import com.navercorp.pinpoint.common.config.util.ValueAnnotationProcessor;
import com.navercorp.pinpoint.grpc.client.config.ClientOption;
import com.navercorp.pinpoint.grpc.client.config.SslOption;

import java.util.function.Function;

/**
 * NOTE module accessibility
 * @see com.navercorp.pinpoint.bootstrap.java9.module.ModuleSupport#addPermissionToValueAnnotation(JavaModule)
 * @author Woonduk Kang(emeroad)
 * @author jaehong.kim
 */
public class GrpcTransportConfig {

    public static final String SYSTEM_PROPERTY_NETTY_TRY_REFLECTION_SET_ACCESSIBLE = "io.netty.tryReflectionSetAccessible";
    public static final String KEY_PROFILER_CONFIG_NETTY_TRY_REFLECTION_SET_ACCESSIBLE = "profiler.system.property." + SYSTEM_PROPERTY_NETTY_TRY_REFLECTION_SET_ACCESSIBLE;

    public static final String SYSTEM_PROPERTY_NETTY_NOPREFERDIRECT = "io.netty.noPreferDirect";
    public static final String KEY_PROFILER_CONFIG_NETTY_NOPREFERDIRECT = "profiler.system.property." + SYSTEM_PROPERTY_NETTY_NOPREFERDIRECT;

    private static final String DEFAULT_IP = "127.0.0.1";
    private static final long DEFAULT_CLIENT_REQUEST_TIMEOUT = 6000;
    private static final int DEFAULT_AGENT_SENDER_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_METADATA_SENDER_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_SPAN_SENDER_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_STAT_SENDER_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_AGENT_COLLECTOR_PORT = 9991;
    private static final int DEFAULT_STAT_COLLECTOR_PORT = 9992;
    private static final int DEFAULT_SPAN_COLLECTOR_PORT = 9993;
    private static final int DEFAULT_AGENT_CHANNEL_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_METADATA_CHANNEL_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_STAT_CHANNEL_EXECUTOR_QUEUE_SIZE = 1000;
    private static final int DEFAULT_SPAN_CHANNEL_EXECUTOR_QUEUE_SIZE = 1000;

    private static final boolean DEFAULT_SSL = false;

    private static final int DEFAULT_DISCARD_LOG_RATE_LIMIT = 100;
    private static final long DEFAULT_DISCARD_MAX_PENDING_THRESHOLD = 1024;
    private static final long DEFAULT_DISCARD_COUNT_FOR_RECONNECT = 1000;
    private static final long DEFAULT_NOT_READY_TIMEOUT_MILLIS = 5 * 60 * 1000;
    private static final long DEFAULT_RPC_MAX_AGE_MILLIS = 3153600000000L; // Disabled

    public final static long DEFAULT_RENEW_TRANSPORT_PERIOD_MILLIS_DISABLE = 3153600000000L;
    private static final long DEFAULT_RENEW_TRANSPORT_PERIOD_MILLIS = DEFAULT_RENEW_TRANSPORT_PERIOD_MILLIS_DISABLE; // Disabled

    private static final int DEFAULT_METADATA_RETRY_MAX_COUNT = 3;
    private static final int DEFAULT_METADATA_RETRY_DELAY_MILLIS = 1000;

    private static final boolean DEFAULT_METADATA_RETRY_ENABLE = false;
    private static final long DEFAULT_METADATA_RETRY_BUFFER_SIZE = 1L << 24;  // 16M
    private static final long DEFAULT_METADATA_PER_RPC_BUFFER_LIMIT = 1L << 20; // 1M
    private static final int DEFAULT_METADATA_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_METADATA_HEDGING_DELAY_MILLIS = 1000;

    public static final boolean DEFAULT_NETTY_SYSTEM_PROPERTY_TRY_REFLECTIVE_SET_ACCESSIBLE = true;

    private static final boolean DEFAULT_ENABLE_SPAN_STATS_LOGGING = false;

    private ClientOption agentClientOption = new ClientOption();
    private ClientOption metadataClientOption = new ClientOption();
    private ClientOption statClientOption = new ClientOption();
    private ClientOption spanClientOption = new ClientOption();

    private SslOption sslOption = null;

    @Value("${profiler.transport.grpc.agent.collector.ip}")
    private String agentCollectorIp = DEFAULT_IP;
    @Value("${profiler.transport.grpc.agent.collector.port}")
    private int agentCollectorPort = DEFAULT_AGENT_COLLECTOR_PORT;
    @Value("${profiler.transport.grpc.agent.ssl.enable}")
    private boolean agentSslEnable = DEFAULT_SSL;
    @Value("${profiler.transport.grpc.agent.sender.request.timeout.millis}")
    private long agentRequestTimeout = DEFAULT_CLIENT_REQUEST_TIMEOUT;
    @Value("${profiler.transport.grpc.agent.sender.executor.queue.size}")
    private int agentSenderExecutorQueueSize = DEFAULT_AGENT_SENDER_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.agent.sender.channel.executor.queue.size}")
    private int agentChannelExecutorQueueSize = DEFAULT_AGENT_CHANNEL_EXECUTOR_QUEUE_SIZE;


    // Metadata
    @Value("${profiler.transport.grpc.metadata.collector.ip}")
    private String metadataCollectorIp = DEFAULT_IP;
    @Value("${profiler.transport.grpc.metadata.collector.port}")
    private int metadataCollectorPort = DEFAULT_AGENT_COLLECTOR_PORT;
    @Value("${profiler.transport.grpc.metadata.ssl.enable}")
    private boolean metadataSslEnable = DEFAULT_SSL;
    @Value("${profiler.transport.grpc.metadata.sender.request.timeout.millis}")
    private long metadataRequestTimeout = DEFAULT_CLIENT_REQUEST_TIMEOUT;
    @Value("${profiler.transport.grpc.metadata.sender.executor.queue.size}")
    private int metadataSenderExecutorQueueSize = DEFAULT_METADATA_SENDER_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.metadata.sender.channel.executor.queue.size}")
    private int metadataChannelExecutorQueueSize = DEFAULT_METADATA_CHANNEL_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.metadata.sender.retry.max.count}")
    private int metadataRetryMaxCount = DEFAULT_METADATA_RETRY_MAX_COUNT;
    @Value("${profiler.transport.grpc.metadata.sender.retry.delay.millis}")
    private int metadataRetryDelayMillis = DEFAULT_METADATA_RETRY_DELAY_MILLIS;
    //grpc client retry
    @Value("${profiler.transport.grpc.metadata.sender.retry.enable}")
    private boolean metadataRetryEnable = DEFAULT_METADATA_RETRY_ENABLE;
    @Value("${profiler.transport.grpc.metadata.sender.retry.buffer.size}")
    private long metadataRetryBufferSize = DEFAULT_METADATA_RETRY_BUFFER_SIZE;
    @Value("${profiler.transport.grpc.metadata.sender.retry.per.rpc.buffer.limit}")
    private long metadataPerRpcBufferLimit = DEFAULT_METADATA_PER_RPC_BUFFER_LIMIT;
    @Value("${profiler.transport.grpc.metadata.sender.max.attempts}")
    private int metadataMaxAttempts = DEFAULT_METADATA_MAX_ATTEMPTS;
    @Value("${profiler.transport.grpc.metadata.sender.hedging.delay.millis}")
    private long metadataHedgingDelayMillis = DEFAULT_METADATA_HEDGING_DELAY_MILLIS;

    @Value("${profiler.transport.grpc.stat.collector.ip}")
    private String statCollectorIp = DEFAULT_IP;
    @Value("${profiler.transport.grpc.stat.collector.port}")
    private int statCollectorPort = DEFAULT_STAT_COLLECTOR_PORT;
    @Value("${profiler.transport.grpc.stat.ssl.enable}")
    private boolean statSslEnable = DEFAULT_SSL;
    @Value("${profiler.transport.grpc.stat.sender.request.timeout.millis}")
    private long statRequestTimeout = DEFAULT_CLIENT_REQUEST_TIMEOUT;
    @Value("${profiler.transport.grpc.stat.sender.executor.queue.size}")
    private int statSenderExecutorQueueSize = DEFAULT_STAT_SENDER_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.stat.sender.channel.executor.queue.size}")
    private int statChannelExecutorQueueSize = DEFAULT_STAT_CHANNEL_EXECUTOR_QUEUE_SIZE;

    @Value("${profiler.transport.grpc.span.collector.ip}")
    private String spanCollectorIp = DEFAULT_IP;
    @Value("${profiler.transport.grpc.span.collector.port}")
    private int spanCollectorPort = DEFAULT_SPAN_COLLECTOR_PORT;
    @Value("${profiler.transport.grpc.span.ssl.enable}")
    private boolean spanSslEnable = DEFAULT_SSL;
    @Value("${profiler.transport.grpc.span.sender.request.timeout.millis}")
    private long spanRequestTimeout = DEFAULT_CLIENT_REQUEST_TIMEOUT;
    @Value("${profiler.transport.grpc.span.sender.executor.queue.size}")
    private int spanSenderExecutorQueueSize = DEFAULT_SPAN_SENDER_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.span.sender.channel.executor.queue.size}")
    private int spanChannelExecutorQueueSize = DEFAULT_SPAN_CHANNEL_EXECUTOR_QUEUE_SIZE;
    @Value("${profiler.transport.grpc.span.stats.logging.enable}")
    private boolean spanEnableStatLogging = DEFAULT_ENABLE_SPAN_STATS_LOGGING;

    @Value("${profiler.transport.grpc.span.sender.discardpolicy.logger.discard.ratelimit}")
    private int spanDiscardLogRateLimit = DEFAULT_DISCARD_LOG_RATE_LIMIT;
    @Value("${profiler.transport.grpc.span.sender.discardpolicy.maxpendingthreshold}")
    private long spanDiscardMaxPendingThreshold = DEFAULT_DISCARD_MAX_PENDING_THRESHOLD;
    @Value("${profiler.transport.grpc.span.sender.discardpolicy.discard-count-for-reconnect}")
    private long spanDiscardCountForReconnect = DEFAULT_DISCARD_COUNT_FOR_RECONNECT;
    @Value("${profiler.transport.grpc.span.sender.discardpolicy.not-ready-timeout-millis}")
    private long spanNotReadyTimeoutMillis = DEFAULT_NOT_READY_TIMEOUT_MILLIS;
    @Value("${profiler.transport.grpc.span.sender.rpc.age.max.millis}")
    private long spanRpcMaxAgeMillis = DEFAULT_RPC_MAX_AGE_MILLIS;

    @Value("${profiler.transport.grpc.loadbalancer.renew.period.millis}")
    private long renewTransportPeriodMillis = DEFAULT_RENEW_TRANSPORT_PERIOD_MILLIS;

    @Value("${" + KEY_PROFILER_CONFIG_NETTY_TRY_REFLECTION_SET_ACCESSIBLE + "}")
    private boolean nettySystemPropertyTryReflectiveSetAccessible = DEFAULT_NETTY_SYSTEM_PROPERTY_TRY_REFLECTIVE_SET_ACCESSIBLE;


    public void read(Function<String, String> properties) {
        ValueAnnotationProcessor reader = new ValueAnnotationProcessor();
        reader.process(this, properties);


        this.agentClientOption = readAgentClientOption(properties);

        // Metadata
        this.metadataClientOption = readMetadataClientOption(properties);

        // Stat
        this.statClientOption = readStatClientOption(properties);

        // Span
        this.spanClientOption = readSpanClientOption(properties);

        // Ssl
        this.sslOption = readSslOption(properties);
    }

    private ClientOption readAgentClientOption(final Function<String, String> properties) {
        return readClientOption(properties, "profiler.transport.grpc.agent.sender.");
    }

    private ClientOption readMetadataClientOption(final Function<String, String> properties) {
        return readClientOption(properties, "profiler.transport.grpc.metadata.sender.");
    }

    private ClientOption readStatClientOption(final Function<String, String> properties) {
        return readClientOption(properties, "profiler.transport.grpc.stat.sender.");
    }

    private ClientOption readSpanClientOption(final Function<String, String> properties) {
        return readClientOption(properties, "profiler.transport.grpc.span.sender.");
    }

    private ClientOption readClientOption(final Function<String, String> properties, final String transportName) {
        final ClientOption clientOption = new ClientOption();

        ValueAnnotationProcessor reader = new ValueAnnotationProcessor();
        reader.process(clientOption, new Function<String, String>() {
            @Override
            public String apply(String placeholderName) {
                String prefix = transportName + placeholderName;
                return properties.apply(prefix);
            }
        });
        return clientOption;
    }

    public SslOption readSslOption(final Function<String, String> properties) {
        final String sslPrefix = "profiler.transport.grpc.ssl.";

        String agentRootPath = properties.apply(DefaultProfilerConfig.AGENT_ROOT_PATH_KEY);

        final SslOption.Builder builder = new SslOption.Builder(agentRootPath);

        ValueAnnotationProcessor reader = new ValueAnnotationProcessor();
        reader.process(builder, new Function<String, String>() {
            @Override
            public String apply(String placeholderName) {
                String prefix = sslPrefix + placeholderName;
                return properties.apply(prefix);
            }
        });
        return builder.build();
    }

    public String getAgentCollectorIp() {
        return agentCollectorIp;
    }

    public int getAgentCollectorPort() {
        return agentCollectorPort;
    }

    public boolean isAgentSslEnable() {
        return agentSslEnable;
    }

    public String getMetadataCollectorIp() {
        return metadataCollectorIp;
    }

    public int getMetadataCollectorPort() {
        return metadataCollectorPort;
    }

    public boolean isMetadataSslEnable() {
        return metadataSslEnable;
    }

    public String getStatCollectorIp() {
        return statCollectorIp;
    }

    public int getStatCollectorPort() {
        return statCollectorPort;
    }

    public boolean isStatSslEnable() {
        return statSslEnable;
    }

    public String getSpanCollectorIp() {
        return spanCollectorIp;
    }

    public int getSpanCollectorPort() {
        return spanCollectorPort;
    }

    public boolean isSpanSslEnable() {
        return spanSslEnable;
    }

    public int getAgentSenderExecutorQueueSize() {
        return agentSenderExecutorQueueSize;
    }

    public int getMetadataSenderExecutorQueueSize() {
        return metadataSenderExecutorQueueSize;
    }

    public int getSpanSenderExecutorQueueSize() {
        return spanSenderExecutorQueueSize;
    }

    public int getStatSenderExecutorQueueSize() {
        return statSenderExecutorQueueSize;
    }

    public int getSpanDiscardLogRateLimit() {
        return spanDiscardLogRateLimit;
    }

    public long getSpanDiscardMaxPendingThreshold() {
        return spanDiscardMaxPendingThreshold;
    }

    public long getSpanDiscardCountForReconnect() {
        return spanDiscardCountForReconnect;
    }

    public long getSpanNotReadyTimeoutMillis() {
        return spanNotReadyTimeoutMillis;
    }

    public long getSpanRpcMaxAgeMillis() {
        return spanRpcMaxAgeMillis;
    }

    public long getRenewTransportPeriodMillis() {
        return renewTransportPeriodMillis;
    }

    public long getAgentRequestTimeout() {
        return agentRequestTimeout;
    }

    public long getMetadataRequestTimeout() {
        return metadataRequestTimeout;
    }

    public long getStatRequestTimeout() {
        return statRequestTimeout;
    }

    public long getSpanRequestTimeout() {
        return spanRequestTimeout;
    }

    public ClientOption getAgentClientOption() {
        return agentClientOption;
    }

    public ClientOption getMetadataClientOption() {
        return metadataClientOption;
    }

    public ClientOption getStatClientOption() {
        return statClientOption;
    }

    public ClientOption getSpanClientOption() {
        return spanClientOption;
    }

    public SslOption getSslOption() {
        return sslOption;
    }

    public int getAgentChannelExecutorQueueSize() {
        return agentChannelExecutorQueueSize;
    }

    public int getMetadataChannelExecutorQueueSize() {
        return metadataChannelExecutorQueueSize;
    }

    public int getStatChannelExecutorQueueSize() {
        return statChannelExecutorQueueSize;
    }

    public int getSpanChannelExecutorQueueSize() {
        return spanChannelExecutorQueueSize;
    }

    public int getMetadataRetryMaxCount() {
        return metadataRetryMaxCount;
    }

    public int getMetadataRetryDelayMillis() {
        return metadataRetryDelayMillis;
    }

    public boolean isMetadataRetryEnable() {
        return metadataRetryEnable;
    }

    public long getMetadataRetryBufferSize() {
        return metadataRetryBufferSize;
    }

    public long getMetadataPerRpcBufferLimit() {
        return metadataPerRpcBufferLimit;
    }

    public int getMetadataMaxAttempts() {
        return metadataMaxAttempts;
    }

    public long getMetadataHedgingDelayMillis() {
        return metadataHedgingDelayMillis;
    }

    public boolean isSpanEnableStatLogging() {
        return spanEnableStatLogging;
    }

    public boolean isNettySystemPropertyTryReflectiveSetAccessible() {
        return nettySystemPropertyTryReflectiveSetAccessible;
    }

    @Override
    public String toString() {
        return "GrpcTransportConfig{" + "agentCollectorIp='" + agentCollectorIp + '\'' +
                ", agentCollectorPort=" + agentCollectorPort +
                ", agentSslEnable=" + agentSslEnable +
                ", metadataCollectorIp='" + metadataCollectorIp + '\'' +
                ", metadataCollectorPort=" + metadataCollectorPort +
                ", metadataSslEnable=" + metadataSslEnable +
                ", statCollectorIp='" + statCollectorIp + '\'' +
                ", statCollectorPort=" + statCollectorPort +
                ", statSslEnable=" + statSslEnable +
                ", spanCollectorIp='" + spanCollectorIp + '\'' +
                ", spanCollectorPort=" + spanCollectorPort +
                ", spanSslEnable=" + spanSslEnable +
                ", agentClientOption=" + agentClientOption +
                ", metadataClientOption=" + metadataClientOption +
                ", statClientOption=" + statClientOption +
                ", spanClientOption=" + spanClientOption +
                ", sslOption=" + sslOption +
                ", agentSenderExecutorQueueSize=" + agentSenderExecutorQueueSize +
                ", metadataSenderExecutorQueueSize=" + metadataSenderExecutorQueueSize +
                ", spanSenderExecutorQueueSize=" + spanSenderExecutorQueueSize +
                ", statSenderExecutorQueueSize=" + statSenderExecutorQueueSize +
                ", agentChannelExecutorQueueSize=" + agentChannelExecutorQueueSize +
                ", metadataChannelExecutorQueueSize=" + metadataChannelExecutorQueueSize +
                ", statChannelExecutorQueueSize=" + statChannelExecutorQueueSize +
                ", spanChannelExecutorQueueSize=" + spanChannelExecutorQueueSize +
                ", agentRequestTimeout=" + agentRequestTimeout +
                ", metadataRequestTimeout=" + metadataRequestTimeout +
                ", spanRequestTimeout=" + spanRequestTimeout +
                ", statRequestTimeout=" + statRequestTimeout +
                ", metadataRetryMaxCount=" + metadataRetryMaxCount +
                ", metadataRetryDelayMillis=" + metadataRetryDelayMillis +
                ", nettySystemPropertyTryReflectiveSetAccessible=" + nettySystemPropertyTryReflectiveSetAccessible +
                ", spanDiscardLogRateLimit=" + spanDiscardLogRateLimit +
                ", spanDiscardMaxPendingThreshold=" + spanDiscardMaxPendingThreshold +
                '}';
    }
}
