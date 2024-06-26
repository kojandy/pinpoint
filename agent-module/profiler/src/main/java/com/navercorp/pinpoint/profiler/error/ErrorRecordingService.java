package com.navercorp.pinpoint.profiler.error;

import com.navercorp.pinpoint.common.trace.ErrorCategory;

public class ErrorRecordingService {
    public boolean isEnabled(ErrorCategory category) {
        return true;
    }
}
