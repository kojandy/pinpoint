/*
 * Copyright 2018 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.websphere.interceptor;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.ws.webcontainer.channel.WCCResponseImpl;
import com.navercorp.pinpoint.bootstrap.config.HttpStatusCodeErrors;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptorHelper;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.ApiIdAwareAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogManager;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogger;
import com.navercorp.pinpoint.bootstrap.plugin.RequestRecorderFactory;
import com.navercorp.pinpoint.bootstrap.plugin.request.RequestAdaptor;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerCookieRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerHeaderRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServletRequestListener;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServletRequestListenerBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.request.util.ParameterRecorder;
import com.navercorp.pinpoint.bootstrap.util.argument.Validation;
import com.navercorp.pinpoint.bootstrap.util.argument.Validator;
import com.navercorp.pinpoint.plugin.websphere.ParameterRecorderFactory;
import com.navercorp.pinpoint.plugin.websphere.StatusCodeAccessor;
import com.navercorp.pinpoint.plugin.websphere.WebsphereConfiguration;
import com.navercorp.pinpoint.plugin.websphere.WebsphereConstants;


/**
 * @author sjmittal
 * @author jaehong.kim
 */
public class WebContainerHandleRequestInterceptor implements ApiIdAwareAroundInterceptor {
    private final PluginLogger logger = PluginLogManager.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final Validator validator;
    private final ServletRequestListener<IRequest> servletRequestListener;

    public WebContainerHandleRequestInterceptor(TraceContext traceContext, RequestRecorderFactory<IRequest> requestRecorderFactory) {
        this.validator = buildValidator();
        final WebsphereConfiguration config = new WebsphereConfiguration(traceContext.getProfilerConfig());
        RequestAdaptor<IRequest> requestAdaptor = new IRequestAdaptor();
        final ParameterRecorder<IRequest> parameterRecorder = ParameterRecorderFactory.newParameterRecorderFactory(config.getExcludeProfileMethodFilter(), config.isTraceRequestParam());

        ServletRequestListenerBuilder<IRequest> builder = new ServletRequestListenerBuilder<>(WebsphereConstants.WEBSPHERE, traceContext, requestAdaptor);
        builder.setExcludeURLFilter(config.getExcludeUrlFilter());
        builder.setTraceExcludeMethodFilter(config.getTraceExcludeMethodFilter());
        builder.setParameterRecorder(parameterRecorder);
        builder.setRequestRecorderFactory(requestRecorderFactory);

        final ProfilerConfig profilerConfig = traceContext.getProfilerConfig();
        builder.setHttpStatusCodeRecorder(HttpStatusCodeErrors.of(profilerConfig::readString));
        builder.setServerHeaderRecorder(profilerConfig.readList(ServerHeaderRecorder.CONFIG_KEY_RECORD_REQ_HEADERS));
        builder.setServerCookieRecorder(profilerConfig.readList(ServerCookieRecorder.CONFIG_KEY_RECORD_REQ_COOKIES));
        this.servletRequestListener = builder.build();
    }

    private Validator buildValidator() {
        Validation argBuilder = new Validation(logger);
        argBuilder.addArgument(IRequest.class, 0);
        argBuilder.addArgument(IResponse.class, 1);
        return argBuilder.build();
    }

    @Override
    public void before(Object target, int apiId, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }

        if (!validator.validate(args)) {
            return;
        }

        try {
            final IRequest request = (IRequest) args[0];
            MethodDescriptor methodDescriptor = MethodDescriptorHelper.apiId(apiId);
            this.servletRequestListener.initialized(request, WebsphereConstants.WEBSPHERE_METHOD, methodDescriptor);
        } catch (Throwable t) {
            logger.info("Failed to servlet request event handle.", t);
        }
    }

    @Override
    public void after(Object target, int apiId, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }

        if (!validator.validate(args)) {
            return;
        }

        try {
            final IRequest request = (IRequest) args[0];
            final IResponse response = (IResponse) args[1];
            final int statusCode = getStatusCode(response);
            this.servletRequestListener.destroyed(request, throwable, statusCode);
        } catch (Throwable t) {
            logger.info("Failed to servlet request event handle.", t);
        }
    }


    private int getStatusCode(final IResponse response) {
        try {
            if (response instanceof StatusCodeAccessor) {
                final StatusCodeAccessor accessor = (StatusCodeAccessor) response;
                return accessor._$PINPOINT$_getStatusCode();
            } else if (response instanceof WCCResponseImpl) {
                WCCResponseImpl r = (WCCResponseImpl) response;
                return r.getHttpResponse().getStatusCodeAsInt();
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}