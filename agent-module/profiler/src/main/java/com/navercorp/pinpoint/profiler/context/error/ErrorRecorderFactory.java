package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.profiler.context.id.TraceRoot;

public class ErrorRecorderFactory {
    public ErrorRecorder newRecorder(TraceRoot traceRoot) {
        return new ErrorRecorder(traceRoot);
    }
}
