package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.id.Shared;

public interface ErrorRecorder {
    void recordError(ErrorCategory category);
    void recordError(ErrorCategory category, Shared shared);
}