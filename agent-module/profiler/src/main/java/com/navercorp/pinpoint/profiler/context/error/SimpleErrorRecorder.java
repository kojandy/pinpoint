package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.bootstrap.context.ErrorRecorder;
import com.navercorp.pinpoint.common.trace.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.id.LocalTraceRoot;

public class SimpleErrorRecorder implements ErrorRecorder {
    private final LocalTraceRoot traceRoot;

    SimpleErrorRecorder(LocalTraceRoot localTraceRoot) {
        this.traceRoot = localTraceRoot;
    }

    @Override
    public void recordError(ErrorCategory errorCategory) {
        traceRoot.getShared().maskErrorCode(1);
    }
}
