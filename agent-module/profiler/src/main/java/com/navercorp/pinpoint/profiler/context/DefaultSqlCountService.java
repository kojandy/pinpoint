package com.navercorp.pinpoint.profiler.context;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.errorhandler.ErrorRecorder;
import com.navercorp.pinpoint.profiler.context.id.Shared;

import java.util.Objects;

public class DefaultSqlCountService implements SqlCountService {
    private final int sqlErrorLimit;
    private final ErrorRecorder errorRecorder;

    public DefaultSqlCountService(int sqlErrorLimit, ErrorRecorder errorRecorder) {
        this.sqlErrorLimit = sqlErrorLimit;
        this.errorRecorder = Objects.requireNonNull(errorRecorder, "errorRecorder");
    }

    @Override
    public void recordSqlCount(Shared shared) {
        boolean isError = shared.getErrorCode() != 0;
        if (isError) {
            return;
        }

        int sqlExecutionCount = shared.incrementAndGetSqlCount();
        if (sqlExecutionCount >= sqlErrorLimit) {
            errorRecorder.recordError(ErrorCategory.SQL_COUNT_EXCEEDED, shared);
        }
    }
}
