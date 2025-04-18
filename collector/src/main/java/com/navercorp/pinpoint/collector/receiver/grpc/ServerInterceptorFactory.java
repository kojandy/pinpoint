package com.navercorp.pinpoint.collector.receiver.grpc;

import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.HeaderReader;
import com.navercorp.pinpoint.grpc.server.HeaderPropagationInterceptor;
import com.navercorp.pinpoint.grpc.server.ServerHeaderReaderFactory;

public class ServerInterceptorFactory {

    public static HeaderPropagationInterceptor headerReader(String name) {
        HeaderReader<Header> headerReader = new ServerHeaderReaderFactory(name);
        return new HeaderPropagationInterceptor(headerReader);
    }
}
