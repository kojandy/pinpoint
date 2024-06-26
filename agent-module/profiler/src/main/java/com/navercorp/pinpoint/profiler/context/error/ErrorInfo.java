package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.common.trace.ErrorCategory;

public class ErrorInfo<T> {
    private final ErrorCategory category;
    private final T content;

    public ErrorInfo(ErrorCategory category, T content) {
        this.category = category;
        this.content = content;
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "category=" + category +
                ", content=" + content +
                '}';
    }
}
