/*
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.openwhisk.interceptor;

import com.navercorp.pinpoint.bootstrap.async.AsyncContextAccessorUtils;
import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogManager;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogger;
import com.navercorp.pinpoint.common.util.ArrayArgumentUtils;
import com.navercorp.pinpoint.plugin.openwhisk.accessor.PinpointTraceAccessor;

/**
 * @author Seonghyun Oh
 */
public class TransactionIdFinishedInterceptor implements AroundInterceptor {

    private final PluginLogger logger = PluginLogManager.getLogger(this.getClass());
    protected final boolean isDebug = logger.isDebugEnabled();

    public TransactionIdFinishedInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
    }

    @Override
    public void before(Object target, Object[] args) {

        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }

        AsyncContext asyncContext = AsyncContextAccessorUtils.getAsyncContext(args, 2);
        Trace trace = getTrace(args);

        if (asyncContext == null || trace == null) {
            return;
        }
        trace.traceBlockEnd();
        trace.close();
        asyncContext.close();
    }

    private Trace getTrace(Object[] args) {
        PinpointTraceAccessor pinpointTraceAccessor = ArrayArgumentUtils.getArgument(args, 2, PinpointTraceAccessor.class);
        if (pinpointTraceAccessor != null) {
            return pinpointTraceAccessor._$PINPOINT$_getPinpointTrace();
        }
        return null;
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {

    }

}

