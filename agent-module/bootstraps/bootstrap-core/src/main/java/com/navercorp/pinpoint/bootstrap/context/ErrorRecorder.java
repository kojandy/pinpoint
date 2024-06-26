package com.navercorp.pinpoint.bootstrap.context;

import com.navercorp.pinpoint.common.trace.ErrorType;

public interface ErrorRecorder {
    void recordError(ErrorType errorType);

    @Deprecated
    default void recordError() {
        recordError(ErrorType.UNKNOWN);
    }
}
