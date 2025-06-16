package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.profiler.context.id.LocalTraceRoot;

public class ErrorRecorder {
    private final LocalTraceRoot traceRoot;

    public ErrorRecorder(LocalTraceRoot localTraceRoot) {
        this.traceRoot = localTraceRoot;
    }

    public void recordError() {
        traceRoot.getShared().maskErrorCode(1);
    }
}
