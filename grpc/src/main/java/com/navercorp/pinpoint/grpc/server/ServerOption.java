/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.grpc.server;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.grpc.ChannelTypeEnum;
import io.netty.buffer.ByteBufAllocator;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author jaehong.kim
 */
public class ServerOption {
    public static final int DEFAULT_FLOW_CONTROL_WINDOW = 1048576; // 1MiB
    public static final long DEFAULT_KEEPALIVE_TIME = TimeUnit.SECONDS.toMillis(30);
    public static final long DEFAULT_KEEPALIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    // Set it to 10 seconds shorter than the client's KeepAliveTime.
    public static final long DEFAULT_PERMIT_KEEPALIVE_TIME = TimeUnit.SECONDS.toMillis(20);
    public static final boolean PERMIT_KEEPALIVE_WITHOUT_CALLS_DISABLE = Boolean.FALSE;

    public static final long DEFAULT_MAX_CONNECTION_IDLE = TimeUnit.SECONDS.toMillis(10); // 10s
    public static final long DEFAULT_MAX_CONNECTION_AGE = Long.MAX_VALUE; // Disabled
    public static final long DEFAULT_MAX_CONNECTION_AGE_GRACE = Long.MAX_VALUE; // Infinite
    public static final int DEFAULT_MAX_CONCURRENT_CALLS_PER_CONNECTION = Integer.MAX_VALUE; // Infinite

    public static final int DEFAULT_MAX_INBOUND_MESSAGE_SIZE = 4 * 1024 * 1024;
    public static final int DEFAULT_MAX_HEADER_LIST_SIZE = 8192;

    public static final long DEFAULT_HANDSHAKE_TIMEOUT = TimeUnit.SECONDS.toMillis(120);
    public static final int DEFAULT_RECEIVE_BUFFER_SIZE = 64 * 1024;

    public static final long DEFAULT_GRPC_MAX_TERM_WAIT_TIME_MILLIS = 3000;

    public static final String DEFAULT_CHANNEL_TYPE = ChannelTypeEnum.AUTO.name();

    // Sets a custom keepalive time, the delay time for sending next keepalive ping.
    private final long keepAliveTime;
    // Sets a custom keepalive timeout, the timeout for keepalive ping requests.
    private final long keepAliveTimeout;
    // Specify the most aggressive keep-alive time clients are permitted to configure.
    private final long permitKeepAliveTime;
    // Sets whether to allow clients to send keep-alive HTTP/2 PINGs even if there are no outstanding RPCs on the connection. Defaults to {@code false}.
    private final boolean permitKeepAliveWithoutCalls = PERMIT_KEEPALIVE_WITHOUT_CALLS_DISABLE;

    // Sets a custom max connection idle time, connection being idle for longer than which will be gracefully terminated.
    private final long maxConnectionIdle;
    // Sets a custom max connection age, connection lasting longer than which will be gracefully terminated.
    private long maxConnectionAge = DEFAULT_MAX_CONNECTION_AGE;
    // Sets a custom grace time for the graceful connection termination. Once the max connection age is reached, RPCs have the grace time to complete.
    private long maxConnectionAgeGrace = DEFAULT_MAX_CONNECTION_AGE_GRACE;

    // The maximum number of concurrent calls permitted for each incoming connection. Defaults to no limit.
    private final int maxConcurrentCallsPerConnection;
    // Sets the maximum message size allowed to be received on the server.
    private final int maxInboundMessageSize;
    // Sets the maximum size of metadata allowed to be received.
    private final int maxHeaderListSize;

    private final long handshakeTimeout;
    // Sets the HTTP/2 flow control window.
    private final int flowControlWindow;

    // ChannelOption
    private final int receiveBufferSize;

    private final ByteBufAllocator allocator;

    private final long grpcMaxTermWaitTimeMillis;

    public final ChannelTypeEnum channelTypeEnum;

    ServerOption(long keepAliveTime, long keepAliveTimeout, long permitKeepAliveTime,
                 long maxConnectionIdle, long maxConnectionAge, long maxConnectionAgeGrace,
                 int maxConcurrentCallsPerConnection, int maxInboundMessageSize, int maxHeaderListSize,
                 long handshakeTimeout, int flowControlWindow, int receiveBufferSize,
                 ByteBufAllocator allocator, long grpcMaxTermWaitTimeMillis,
                 ChannelTypeEnum channelTypeEnum) {
        this.keepAliveTime = keepAliveTime;
        this.keepAliveTimeout = keepAliveTimeout;
        this.permitKeepAliveTime = permitKeepAliveTime;

        this.maxConnectionIdle = maxConnectionIdle;
        this.maxConnectionAge = maxConnectionAge;
        this.maxConnectionAgeGrace = maxConnectionAgeGrace;

        this.maxConcurrentCallsPerConnection = maxConcurrentCallsPerConnection;
        this.maxInboundMessageSize = maxInboundMessageSize;
        this.maxHeaderListSize = maxHeaderListSize;
        this.handshakeTimeout = handshakeTimeout;
        this.flowControlWindow = flowControlWindow;
        this.receiveBufferSize = receiveBufferSize;
        this.allocator = allocator;
        this.grpcMaxTermWaitTimeMillis = grpcMaxTermWaitTimeMillis;
        this.channelTypeEnum = Objects.requireNonNull(channelTypeEnum, "channelTypeEnum");
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public long getPermitKeepAliveTime() {
        return permitKeepAliveTime;
    }

    public boolean isPermitKeepAliveWithoutCalls() {
        return permitKeepAliveWithoutCalls;
    }

    public long getMaxConnectionIdle() {
        return maxConnectionIdle;
    }

    public long getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public long getMaxConnectionAgeGrace() {
        return maxConnectionAgeGrace;
    }

    public int getMaxConcurrentCallsPerConnection() {
        return maxConcurrentCallsPerConnection;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public int getMaxHeaderListSize() {
        return maxHeaderListSize;
    }

    public long getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public int getFlowControlWindow() {
        return flowControlWindow;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public ByteBufAllocator getAllocator() {
        return allocator;
    }

    public long getGrpcMaxTermWaitTimeMillis() {
        return grpcMaxTermWaitTimeMillis;
    }

    public ChannelTypeEnum getChannelTypeEnum() {
        return channelTypeEnum;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ServerOption{" +
                "keepAliveTime=" + keepAliveTime +
                ", keepAliveTimeout=" + keepAliveTimeout +
                ", permitKeepAliveTime=" + permitKeepAliveTime +
                ", permitKeepAliveWithoutCalls=" + permitKeepAliveWithoutCalls +
                ", maxConnectionIdle=" + maxConnectionIdle +
                ", maxConnectionAge=" + maxConnectionAge +
                ", maxConnectionAgeGrace=" + maxConnectionAgeGrace +
                ", maxConcurrentCallsPerConnection=" + maxConcurrentCallsPerConnection +
                ", maxInboundMessageSize=" + maxInboundMessageSize +
                ", maxHeaderListSize=" + maxHeaderListSize +
                ", handshakeTimeout=" + handshakeTimeout +
                ", flowControlWindow=" + flowControlWindow +
                ", receiveBufferSize=" + receiveBufferSize +
                ", grpcMaxTermWaitTimeMillis=" + grpcMaxTermWaitTimeMillis +
                ", channelTypeEnum=" + channelTypeEnum +
                '}';
    }

    public static class Builder {
        // Sets a custom keepalive time, the delay time for sending next keepalive ping.
        private long keepAliveTime = DEFAULT_KEEPALIVE_TIME;
        // Sets a custom keepalive timeout, the timeout for keepalive ping requests.
        private long keepAliveTimeout = DEFAULT_KEEPALIVE_TIMEOUT;
        // Specify the most aggressive keep-alive time clients are permitted to configure.
        private long permitKeepAliveTime = DEFAULT_PERMIT_KEEPALIVE_TIME;

        // Sets a custom max connection idle time, connection being idle for longer than which will be gracefully terminated.
        private long maxConnectionIdle = DEFAULT_MAX_CONNECTION_IDLE;
        private long maxConnectionAge = DEFAULT_MAX_CONNECTION_AGE;
        private long maxConnectionAgeGrace = DEFAULT_MAX_CONNECTION_AGE_GRACE;

        // The maximum number of concurrent calls permitted for each incoming connection. Defaults to no limit.
        private int maxConcurrentCallsPerConnection = DEFAULT_MAX_CONCURRENT_CALLS_PER_CONNECTION;

        // Sets the maximum message size allowed to be received on the server.
        private int maxInboundMessageSize = DEFAULT_MAX_INBOUND_MESSAGE_SIZE;
        // Sets the maximum size of metadata allowed to be received.
        private int maxHeaderListSize = DEFAULT_MAX_HEADER_LIST_SIZE;

        private long handshakeTimeout = DEFAULT_HANDSHAKE_TIMEOUT;
        // Sets the HTTP/2 flow control window.
        private int flowControlWindow = DEFAULT_FLOW_CONTROL_WINDOW;

        private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;

        private ByteBufAllocator allocator;

        private long grpcMaxTermWaitTimeMillis = DEFAULT_GRPC_MAX_TERM_WAIT_TIME_MILLIS;

        private ChannelTypeEnum channelTypeEnum = ChannelTypeEnum.valueOf(DEFAULT_CHANNEL_TYPE);

        private Builder() {
        }

        public ServerOption build() {
            return new ServerOption(keepAliveTime, keepAliveTimeout, permitKeepAliveTime,
                    maxConnectionIdle, maxConnectionAge, maxConnectionAgeGrace,
                    maxConcurrentCallsPerConnection, maxInboundMessageSize,
                    maxHeaderListSize, handshakeTimeout, flowControlWindow, receiveBufferSize, allocator,
                    grpcMaxTermWaitTimeMillis, channelTypeEnum);
        }

        public void setKeepAliveTime(long keepAliveTime) {
            Assert.isTrue(keepAliveTime > 0, "keepAliveTime " + keepAliveTime + " must be positive");
            this.keepAliveTime = keepAliveTime;
        }

        public void setKeepAliveTimeout(long keepAliveTimeout) {
            Assert.isTrue(keepAliveTimeout > 0, "keepAliveTimeout " + keepAliveTimeout + " must be positive");
            this.keepAliveTimeout = keepAliveTimeout;
        }

        public void setPermitKeepAliveTime(long permitKeepAliveTime) {
            Assert.isTrue(permitKeepAliveTime >= 0, "permitKeepAliveTime " + permitKeepAliveTime + " must be non-negative");
            this.permitKeepAliveTime = permitKeepAliveTime;
        }

        public void setMaxConnectionIdle(long maxConnectionIdle) {
            Assert.isTrue(maxConnectionIdle > 0, "maxConnectionIdle " + maxConnectionIdle + " must be positive");
            this.maxConnectionIdle = maxConnectionIdle;
        }

        public void setMaxConnectionAge(long maxConnectionAge) {
            Assert.isTrue(maxConnectionAge > 0, "maxConnectionAge " + maxConnectionAge + " must be positive");
            this.maxConnectionAge = maxConnectionAge;
        }

        public void setMaxConnectionAgeGrace(long maxConnectionAgeGrace) {
            Assert.isTrue(maxConnectionAgeGrace > 0, "maxConnectionAgeGrace " + maxConnectionAgeGrace + " must be positive");
            this.maxConnectionAgeGrace = maxConnectionAgeGrace;
        }

        public void setMaxConcurrentCallsPerConnection(int maxConcurrentCallsPerConnection) {
            Assert.isTrue(maxConcurrentCallsPerConnection > 0, "maxConcurrentCallsPerConnection " + maxConcurrentCallsPerConnection + " must be positive");
            this.maxConcurrentCallsPerConnection = maxConcurrentCallsPerConnection;
        }

        public void setMaxInboundMessageSize(int maxInboundMessageSize) {
            Assert.isTrue(maxInboundMessageSize > 0, "maxInboundMessageSize " + maxInboundMessageSize + " must be positive");
            this.maxInboundMessageSize = maxInboundMessageSize;
        }

        public void setMaxHeaderListSize(int maxHeaderListSize) {
            Assert.isTrue(maxHeaderListSize > 0, "maxHeaderListSize " + maxHeaderListSize + " must be positive");
            this.maxHeaderListSize = maxHeaderListSize;
        }

        public void setHandshakeTimeout(long handshakeTimeout) {
            Assert.isTrue(handshakeTimeout > 0, "handshakeTimeout " + handshakeTimeout + " must be positive");
            this.handshakeTimeout = handshakeTimeout;
        }

        public void setFlowControlWindow(int flowControlWindow) {
            Assert.isTrue(flowControlWindow > 0, "flowControlWindow " + flowControlWindow + " must be positive");
            this.flowControlWindow = flowControlWindow;
        }

        public void setReceiveBufferSize(int receiveBufferSize) {
            Assert.isTrue(receiveBufferSize > 0, "receiveBufferSize " + receiveBufferSize + " must be positive");
            this.receiveBufferSize = receiveBufferSize;
        }

        public void setAllocator(ByteBufAllocator allocator) {
            this.allocator = allocator;
        }

        public void setGrpcMaxTermWaitTimeMillis(long grpcMaxTermWaitTimeMillis) {
            Assert.isTrue(grpcMaxTermWaitTimeMillis > 0, "grpcMaxTermWaitTimeMillis " + grpcMaxTermWaitTimeMillis + " must be positive");
            this.grpcMaxTermWaitTimeMillis = grpcMaxTermWaitTimeMillis;
        }

        public void setChannelTypeEnum(String channelTypeEnum) {
            Objects.requireNonNull(channelTypeEnum, "channelTypeEnum");
            this.channelTypeEnum = ChannelTypeEnum.valueOf(channelTypeEnum);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "keepAliveTime=" + keepAliveTime +
                    ", keepAliveTimeout=" + keepAliveTimeout +
                    ", permitKeepAliveTime=" + permitKeepAliveTime +
                    ", maxConnectionIdle=" + maxConnectionIdle +
                    ", maxConnectionAge=" + maxConnectionAge +
                    ", maxConnectionAgeGrace=" + maxConnectionAgeGrace +
                    ", maxConcurrentCallsPerConnection=" + maxConcurrentCallsPerConnection +
                    ", maxInboundMessageSize=" + maxInboundMessageSize +
                    ", maxHeaderListSize=" + maxHeaderListSize +
                    ", handshakeTimeout=" + handshakeTimeout +
                    ", flowControlWindow=" + flowControlWindow +
                    ", receiveBufferSize=" + receiveBufferSize +
                    ", grpcMaxTermWaitTimeMillis=" + grpcMaxTermWaitTimeMillis +
                    ", channelTypeEnum=" + channelTypeEnum +
                    '}';
        }
    }
}