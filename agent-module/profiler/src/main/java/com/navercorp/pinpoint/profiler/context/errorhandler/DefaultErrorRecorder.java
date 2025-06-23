package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.id.Shared;

import java.util.Objects;

public class DefaultErrorRecorder implements ErrorRecorder {
    
    private final ErrorCategoryManager errorCategoryManager;
    private final ErrorMarker errorMarker;
    
    public DefaultErrorRecorder(ErrorCategoryManager errorCategoryManager,
                              ErrorMarker errorMarker) {
        this.errorCategoryManager = Objects.requireNonNull(errorCategoryManager, "errorCategoryManager");
        this.errorMarker = Objects.requireNonNull(errorMarker, "errorMarker");
    }
    
    @Override
    public void recordError(ErrorCategory category) {
        Objects.requireNonNull(category, "category");
        
        if (errorCategoryManager.shouldRecordError(category)) {
            errorMarker.markError(1);
        }
    }
    
    @Override
    public void recordError(ErrorCategory category, Shared shared) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(shared, "shared");
        
        if (errorCategoryManager.shouldRecordError(category)) {
            shared.maskErrorCode(1);
        }
    }
    
    public interface ErrorMarker {
        void markError(int errorCode);
    }
}