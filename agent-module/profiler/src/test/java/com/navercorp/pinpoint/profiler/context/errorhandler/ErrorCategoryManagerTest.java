package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ErrorCategoryManagerTest {

    @Mock
    private ErrorCategoryConfig config;
    
    @Mock
    private IgnoreErrorHandler ignoreErrorHandler;
    
    private ErrorCategoryManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new ErrorCategoryManager(config, ignoreErrorHandler);
    }

    @Test
    void testShouldRecordError() {
        when(config.isEnabled(ErrorCategory.HTTP_STATUS_CODE)).thenReturn(true);
        when(config.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED)).thenReturn(false);

        assertTrue(manager.shouldRecordError(ErrorCategory.HTTP_STATUS_CODE));
        assertFalse(manager.shouldRecordError(ErrorCategory.SQL_COUNT_EXCEEDED));
    }

    @Test
    void testShouldRecordException_CategoryDisabled() {
        when(config.isEnabled(ErrorCategory.EXCEPTION)).thenReturn(false);

        Exception testException = new RuntimeException("Test exception");
        assertFalse(manager.shouldRecordException(testException));
        
        // IgnoreErrorHandler should not be called if category is disabled
        verify(ignoreErrorHandler, never()).handleError(any());
    }

    @Test
    void testShouldRecordException_CategoryEnabledExceptionIgnored() {
        when(config.isEnabled(ErrorCategory.EXCEPTION)).thenReturn(true);
        when(ignoreErrorHandler.handleError(any())).thenReturn(true); // Exception should be ignored

        Exception testException = new RuntimeException("Test exception");
        assertFalse(manager.shouldRecordException(testException));
        
        verify(ignoreErrorHandler).handleError(testException);
    }

    @Test
    void testShouldRecordException_CategoryEnabledExceptionNotIgnored() {
        when(config.isEnabled(ErrorCategory.EXCEPTION)).thenReturn(true);
        when(ignoreErrorHandler.handleError(any())).thenReturn(false); // Exception should not be ignored

        Exception testException = new RuntimeException("Test exception");
        assertTrue(manager.shouldRecordException(testException));
        
        verify(ignoreErrorHandler).handleError(testException);
    }

    @Test
    void testShouldRecordException_NullException() {
        when(config.isEnabled(ErrorCategory.EXCEPTION)).thenReturn(true);

        assertTrue(manager.shouldRecordException(null));
        
        // IgnoreErrorHandler should not be called for null exceptions
        verify(ignoreErrorHandler, never()).handleError(any());
    }

    @Test
    void testShouldRecordHttpStatusError() {
        when(config.isEnabled(ErrorCategory.HTTP_STATUS_CODE)).thenReturn(true);
        assertTrue(manager.shouldRecordHttpStatusError());

        when(config.isEnabled(ErrorCategory.HTTP_STATUS_CODE)).thenReturn(false);
        assertFalse(manager.shouldRecordHttpStatusError());
    }

    @Test
    void testShouldRecordSqlCountError() {
        when(config.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED)).thenReturn(true);
        assertTrue(manager.shouldRecordSqlCountError());

        when(config.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED)).thenReturn(false);
        assertFalse(manager.shouldRecordSqlCountError());
    }

    @Test
    void testShouldRecordCustomError() {
        when(config.isEnabled(ErrorCategory.CUSTOM)).thenReturn(true);
        assertTrue(manager.shouldRecordCustomError());

        when(config.isEnabled(ErrorCategory.CUSTOM)).thenReturn(false);
        assertFalse(manager.shouldRecordCustomError());
    }

    @Test
    void testGetConfig() {
        assertSame(config, manager.getConfig());
    }

    @Test
    void testConstructorNullConfig() {
        assertThrows(NullPointerException.class, () -> {
            new ErrorCategoryManager(null, ignoreErrorHandler);
        });
    }

    @Test
    void testConstructorNullIgnoreErrorHandler() {
        assertThrows(NullPointerException.class, () -> {
            new ErrorCategoryManager(config, null);
        });
    }

    @Test
    void testShouldRecordErrorNullCategory() {
        assertThrows(NullPointerException.class, () -> {
            manager.shouldRecordError(null);
        });
    }
}