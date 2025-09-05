package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.bootstrap.context.ErrorRecorder;
import com.navercorp.pinpoint.common.trace.ErrorType;

public class ConfigurableErrorRecorder implements ErrorRecorder {
    @Override
    public void recordError(ErrorType errorType) {
        throw new UnsupportedOperationException();
    }
}
