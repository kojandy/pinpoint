package com.navercorp.pinpoint.common.trace;

public class ErrorCategoryFactory {
    public static ErrorCategory of(int code, String name) {
        return new DefaultErrorCategory(code, name);
    }
}
