/*
 * Copyright 2016 Pinpoint contributors and NAVER Corp.
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
package com.navercorp.pinpoint.plugin.jboss;

import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.Header;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogManager;
import com.navercorp.pinpoint.bootstrap.plugin.RequestRecorderFactory;
import com.navercorp.pinpoint.bootstrap.plugin.proxy.DisableRequestRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.RequestAdaptor;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.plugin.jboss.interceptor.StandardHostValveInvokeInterceptor;
import com.navercorp.pinpoint.profiler.context.DefaultMethodDescriptor;
import com.navercorp.pinpoint.profiler.context.id.DefaultTraceId;
import com.navercorp.pinpoint.profiler.context.module.DefaultApplicationContext;
import com.navercorp.pinpoint.profiler.logging.Log4j2Binder;
import com.navercorp.pinpoint.profiler.test.MockTraceContextFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class InvokeMethodInterceptorTest.
 *
 * @author emeroad
 */
@ExtendWith(MockitoExtension.class)
public class InvokeMethodInterceptorTest {

    /**
     * The request.
     */
    @Mock
    public HttpServletRequest request;

    /**
     * The response.
     */
    @Mock
    public HttpServletResponse response;

    /**
     * The descriptor.
     */
    private final MethodDescriptor descriptor = new DefaultMethodDescriptor("org.apache.catalina.core.StandardHostValve", "invoke", new String[]{
            "org.apache.catalina.connector.Request", "org.apache.catalina.connector.Response"}, new String[]{"request", "response"}, 0);

    private DefaultApplicationContext applicationContext;

    @Mock
    private RequestRecorderFactory<HttpServletRequest> requestRecorderFactory;


    /**
     * Before.
     */
    @BeforeAll
    public static void before() {
        LoggerContext context = LogManager.getContext();
        PluginLogManager.initialize(new Log4j2Binder(context));
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeEach
    public void beforeEach() {

        when(requestRecorderFactory.getProxyRequestRecorder(any(RequestAdaptor.class)))
                .thenReturn(new DisableRequestRecorder<HttpServletRequest>());

        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        applicationContext = MockTraceContextFactory.newMockApplicationContext(profilerConfig);
    }


    @AfterEach
    public void tearDown() throws Exception {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    private TraceContext spyTraceContext() {
        TraceContext traceContext = applicationContext.getTraceContext();
        return spy(traceContext);
    }

    /**
     * Test header not exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHeaderNOTExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn(null);
        lenient().when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn(null);
        lenient().when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn(null);
        lenient().when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn(null);
        lenient().when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn(null);
        final Enumeration<?> enumeration = mock(Enumeration.class);
        lenient().when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, requestRecorderFactory);
        final int apiId = 1;

        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext).newAsyncTraceObject(anyString());

        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext, times(2)).newAsyncTraceObject(anyString());
    }

    /**
     * Test invalid header exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidHeaderExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        lenient().when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn("TRACEID");
        when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn("false");
        when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn("0");
        final Enumeration<?> enumeration = mock(Enumeration.class);
        lenient().when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, requestRecorderFactory);
        final int apiId = 1;

        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext, never()).newTraceObject(anyString());
        verify(traceContext, never()).disableSampling();
        verify(traceContext, never()).continueTraceObject(any(TraceId.class));


        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext, never()).newTraceObject(anyString());
        verify(traceContext, never()).disableSampling();
        verify(traceContext, never()).continueTraceObject(any(TraceId.class));
    }

    /**
     * Test valid header exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testValidHeaderExists() {

        when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        TraceId traceId = new DefaultTraceId(TransactionId.of("agentTest", System.currentTimeMillis(), 1));
        when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn(traceId.getTransactionId());
        when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        when(request.getHeader(Header.HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        when(request.getHeader(Header.HTTP_SAMPLED.toString())).thenReturn("false");
        when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn("0");
        final Enumeration<?> enumeration = mock(Enumeration.class);
        lenient().when(request.getParameterNames()).thenReturn((Enumeration<String>) enumeration);

        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, requestRecorderFactory);
        final int apiId = 1;

        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext).continueAsyncTraceObject((any(TraceId.class)));

        interceptor.before("target", apiId, new Object[]{request, response});
        interceptor.after("target", apiId, new Object[]{request, response}, new Object(), null);

        verify(traceContext, times(2)).continueAsyncTraceObject(any(TraceId.class));
    }
}
