package com.navercorp.pinpoint.common.trace;

public interface ErrorCategory {
    int getCode();
    String getName();

    ErrorCategory UNKNOWN = ErrorCategoryFactory.of(-1, "UNKNOWN");
    ErrorCategory EXCEPTION = ErrorCategoryFactory.of(1, "EXCEPTION");
    ErrorCategory HTTP_STATUS = ErrorCategoryFactory.of(2, "HTTP-STATUS");
    ErrorCategory SQL_COUNT = ErrorCategoryFactory.of(3, "SQL-COUNT");
}
