package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.bootstrap.context.ErrorRecorder;
import com.navercorp.pinpoint.common.trace.ErrorType;
import com.navercorp.pinpoint.profiler.context.id.LocalTraceRoot;

public class SimpleErrorRecorder implements ErrorRecorder {
    private final LocalTraceRoot traceRoot;

    SimpleErrorRecorder(LocalTraceRoot localTraceRoot) {
        this.traceRoot = localTraceRoot;
    }

    @Override
    public void recordError(ErrorType errorType) {
        traceRoot.getShared().maskErrorCode(1);
    }
}
