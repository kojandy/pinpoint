package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.bootstrap.context.ErrorRecorder;
import com.navercorp.pinpoint.common.trace.ErrorCategory;

public class ConfigurableErrorRecorder implements ErrorRecorder {
    @Override
    public void recordError(ErrorCategory errorCategory) {
        throw new UnsupportedOperationException();
    }
}
