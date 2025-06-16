package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.bootstrap.context.ErrorRecorder;
import com.navercorp.pinpoint.profiler.context.id.LocalTraceRoot;

public class ErrorRecorderFactory {
    public ErrorRecorder newRecorder(LocalTraceRoot traceRoot) {
        return new SimpleErrorRecorder(traceRoot);
    }
}
