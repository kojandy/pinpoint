package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;

import java.util.Objects;

public class ErrorCategoryManager {
    private final ErrorCategoryConfig config;
    private final IgnoreErrorHandler ignoreErrorHandler;
    
    public ErrorCategoryManager(ErrorCategoryConfig config, IgnoreErrorHandler ignoreErrorHandler) {
        this.config = Objects.requireNonNull(config, "config");
        this.ignoreErrorHandler = Objects.requireNonNull(ignoreErrorHandler, "ignoreErrorHandler");
    }
    
    public boolean shouldRecordError(ErrorCategory category) {
        Objects.requireNonNull(category, "category");
        return config.isEnabled(category);
    }
    
    public boolean shouldRecordException(Throwable throwable) {
        if (!shouldRecordError(ErrorCategory.EXCEPTION)) {
            return false;
        }
        
        if (throwable == null) {
            return true;
        }
        
        // Check if this specific exception should be ignored
        return !ignoreErrorHandler.handleError(throwable);
    }
    
    public boolean shouldRecordHttpStatusError() {
        return shouldRecordError(ErrorCategory.HTTP_STATUS_CODE);
    }
    
    public boolean shouldRecordSqlCountError() {
        return shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED);
    }
    
    public boolean shouldRecordCustomError() {
        return shouldRecordError(ErrorCategory.CUSTOM);
    }
    
    public ErrorCategoryConfig getConfig() {
        return config;
    }
}