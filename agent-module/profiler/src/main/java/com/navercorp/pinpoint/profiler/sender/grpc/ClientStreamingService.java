package com.navercorp.pinpoint.profiler.sender.grpc;

import com.navercorp.pinpoint.grpc.stream.ClientCallStateStreamObserver;
import com.navercorp.pinpoint.profiler.sender.grpc.stream.ClientStreamingProvider;
import com.navercorp.pinpoint.profiler.sender.grpc.stream.StreamJob;

import java.util.Objects;

public class ClientStreamingService<ReqT, ResT> {
    private final ClientStreamingProvider<ReqT, ResT> clientStreamingProvider;
    private final Reconnector reconnector;

    public ClientStreamingService(ClientStreamingProvider<ReqT, ResT> clientStreamingProvider,
                                  Reconnector reconnector) {
        this.clientStreamingProvider = Objects.requireNonNull(clientStreamingProvider, "clientStreamingProvider");
        this.reconnector = Objects.requireNonNull(reconnector, "reconnector");
    }

    public ClientCallStateStreamObserver<ReqT> newStream(StreamJob<ReqT> streamJob) {
        ResponseStreamObserver<ReqT, ResT> response = newResponse(streamJob);
        return clientStreamingProvider.newStream(response);
    }


    private ResponseStreamObserver<ReqT, ResT> newResponse(StreamJob<ReqT> streamJob) {
        StreamEventListener<ReqT> listener = new DefaultStreamEventListener<>(reconnector, streamJob);
        return new ResponseStreamObserver<>(listener);
    }
}
