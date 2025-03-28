/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.grpc.mapper;

import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.grpc.trace.PTransactionId;
import com.navercorp.pinpoint.profiler.context.id.DefaultTraceId;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author intr3p1d
 */
public class TraceIdMapStructUtils {
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface ToTransactionId {
    }

    @ToTransactionId
    public static PTransactionId newTransactionId(TraceId traceId) {
        if (traceId instanceof DefaultTraceId) {
            DefaultTraceId defaultTraceId = (DefaultTraceId) traceId;
            TransactionId txId = defaultTraceId.getInternalTransactionId();
            final PTransactionId.Builder builder = PTransactionId.newBuilder();
            builder.setAgentId(txId.getAgentId());
            builder.setAgentStartTime(txId.getAgentStartTime());
            builder.setSequence(txId.getTransactionSequence());
            return builder.build();
        }
        throw new IllegalArgumentException("Unexpected TraceId type: " + traceId);
    }
}
