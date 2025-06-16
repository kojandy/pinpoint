package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.profiler.context.id.TraceRoot;

public class ErrorRecorder {
    public void recordError(TraceRoot traceRoot) {
        traceRoot.getShared().maskErrorCode(1);
    }
}
