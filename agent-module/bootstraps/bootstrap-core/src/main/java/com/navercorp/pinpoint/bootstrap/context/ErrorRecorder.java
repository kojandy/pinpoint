package com.navercorp.pinpoint.bootstrap.context;

import com.navercorp.pinpoint.common.trace.ErrorCategory;

public interface ErrorRecorder {
    void maskErrorCode(int errorCode);

    void recordError(ErrorCategory category, String content);

    void recordError(ErrorCategory category, int content);
}
