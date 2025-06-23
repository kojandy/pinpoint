package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DefaultErrorRecorderTest {

    @Mock
    private ErrorCategoryManager errorCategoryManager;
    
    @Mock
    private DefaultErrorRecorder.ErrorMarker errorMarker;
    
    @Mock
    private Shared shared;
    
    private DefaultErrorRecorder errorRecorder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        errorRecorder = new DefaultErrorRecorder(errorCategoryManager, errorMarker);
    }

    @Test
    void testRecordError_CategoryEnabled() {
        when(errorCategoryManager.shouldRecordError(ErrorCategory.HTTP_STATUS_CODE)).thenReturn(true);

        errorRecorder.recordError(ErrorCategory.HTTP_STATUS_CODE);

        verify(errorCategoryManager).shouldRecordError(ErrorCategory.HTTP_STATUS_CODE);
        verify(errorMarker).markError(1);
    }

    @Test
    void testRecordError_CategoryDisabled() {
        when(errorCategoryManager.shouldRecordError(ErrorCategory.HTTP_STATUS_CODE)).thenReturn(false);

        errorRecorder.recordError(ErrorCategory.HTTP_STATUS_CODE);

        verify(errorCategoryManager).shouldRecordError(ErrorCategory.HTTP_STATUS_CODE);
        verify(errorMarker, never()).markError(anyInt());
    }

    @Test
    void testRecordError_NullCategory() {
        assertThrows(NullPointerException.class, () -> {
            errorRecorder.recordError(null);
        });
    }

    @Test
    void testConstructor_NullErrorCategoryManager() {
        assertThrows(NullPointerException.class, () -> {
            new DefaultErrorRecorder(null, errorMarker);
        });
    }

    @Test
    void testConstructor_NullErrorMarker() {
        assertThrows(NullPointerException.class, () -> {
            new DefaultErrorRecorder(errorCategoryManager, null);
        });
    }

    @Test
    void testRecordError_WithShared_CategoryEnabled() {
        when(errorCategoryManager.shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED)).thenReturn(true);

        errorRecorder.recordError(ErrorCategory.SQL_COUNT_EXCEEDED, shared);

        verify(errorCategoryManager).shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED);
        verify(shared).maskErrorCode(1);
    }

    @Test
    void testRecordError_WithShared_CategoryDisabled() {
        when(errorCategoryManager.shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED)).thenReturn(false);

        errorRecorder.recordError(ErrorCategory.SQL_COUNT_EXCEEDED, shared);

        verify(errorCategoryManager).shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED);
        verify(shared, never()).maskErrorCode(anyInt());
    }

    @Test
    void testRecordError_WithShared_NullCategory() {
        assertThrows(NullPointerException.class, () -> {
            errorRecorder.recordError(null, shared);
        });
    }

    @Test
    void testRecordError_WithShared_NullShared() {
        assertThrows(NullPointerException.class, () -> {
            errorRecorder.recordError(ErrorCategory.SQL_COUNT_EXCEEDED, null);
        });
    }
}