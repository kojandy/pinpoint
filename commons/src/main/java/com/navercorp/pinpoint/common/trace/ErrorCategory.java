package com.navercorp.pinpoint.common.trace;

public interface ErrorCategory {
    int getCode();
    String getName();

    ErrorCategory UNKNOWN = ErrorCategoryFactory.of(-1, "UNKNOWN");

    // exception
    ErrorCategory EXCEPTION = ErrorCategoryFactory.of(101, "EXCEPTION");

    // application status
    ErrorCategory HTTP_STATUS = ErrorCategoryFactory.of(201, "HTTP-STATUS");

    // database
    ErrorCategory SQL_COUNT = ErrorCategoryFactory.of(301, "SQL-COUNT");
}
