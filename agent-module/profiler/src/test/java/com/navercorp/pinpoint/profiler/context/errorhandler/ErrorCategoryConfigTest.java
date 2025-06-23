package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ErrorCategoryConfigTest {

    @Test
    void testDefaultConfiguration() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("exception,http-status");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertFalse(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testAllCategoriesEnabled() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("all");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        for (ErrorCategory category : ErrorCategory.values()) {
            assertTrue(errorConfig.isEnabled(category), 
                    "Category " + category + " should be enabled when 'all' is specified");
        }
    }

    @Test
    void testNoCategoriesEnabled() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("none");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        for (ErrorCategory category : ErrorCategory.values()) {
            assertFalse(errorConfig.isEnabled(category), 
                    "Category " + category + " should be disabled when 'none' is specified");
        }
    }

    @Test
    void testSpecificCategoriesEnabled() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("exception,sql-count");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertFalse(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertTrue(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testCaseInsensitiveConfiguration() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("EXCEPTION,Http-Status,SQL-Count");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertTrue(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testConfigurationWithSpaces() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn(" exception , http-status , sql-count ");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertTrue(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testEmptyConfiguration() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        // Should fall back to default
        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertFalse(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testNullConfiguration() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn(null);

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        // Should fall back to default
        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertFalse(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testInvalidCategoryIgnored() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("exception,invalid-category,http-status");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));
        assertFalse(errorConfig.isEnabled(ErrorCategory.SQL_COUNT_EXCEEDED));
        assertFalse(errorConfig.isEnabled(ErrorCategory.CUSTOM));
    }

    @Test
    void testSetEnabled() {
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        when(config.readString("profiler.error.mark", "exception,http-status"))
                .thenReturn("exception");

        ErrorCategoryConfig errorConfig = new ErrorCategoryConfig(config);

        assertTrue(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
        assertFalse(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));

        // Dynamically enable HTTP_STATUS_CODE
        errorConfig.setEnabled(ErrorCategory.HTTP_STATUS_CODE, true);
        assertTrue(errorConfig.isEnabled(ErrorCategory.HTTP_STATUS_CODE));

        // Dynamically disable EXCEPTION
        errorConfig.setEnabled(ErrorCategory.EXCEPTION, false);
        assertFalse(errorConfig.isEnabled(ErrorCategory.EXCEPTION));
    }
}