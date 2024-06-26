package com.navercorp.pinpoint.common.trace;

public interface ErrorCategory {
    int getCode();
    String getName();

    ErrorCategory EXCEPTION = ErrorCategoryFactory.of(1, "EXCEPTION");
    ErrorCategory HTTP_STATUS = ErrorCategoryFactory.of(2, "HTTP-STATUS");
    ErrorCategory SQL_COUNT = ErrorCategoryFactory.of(3, "SQL-COUNT");
}
