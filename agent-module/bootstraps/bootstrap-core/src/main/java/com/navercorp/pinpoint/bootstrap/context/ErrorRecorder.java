package com.navercorp.pinpoint.bootstrap.context;

public interface ErrorRecorder {
    @Deprecated
    default void recordError() {
        maskErrorCode(1);
    }

    void maskErrorCode(final int errorCode);
}
