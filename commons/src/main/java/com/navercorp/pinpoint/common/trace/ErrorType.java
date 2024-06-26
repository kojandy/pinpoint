package com.navercorp.pinpoint.common.trace;

public enum ErrorType {
    UNKNOWN(1),
    EXCEPTION(2),
    HTTP_STATUS(3),
    SQL(4);

    private final int code;

    ErrorType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
