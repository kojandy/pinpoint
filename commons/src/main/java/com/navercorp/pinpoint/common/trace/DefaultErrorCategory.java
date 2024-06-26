package com.navercorp.pinpoint.common.trace;

public class DefaultErrorCategory implements ErrorCategory {
    private final int code;
    private final String name;

    DefaultErrorCategory(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DefaultErrorCategory{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
