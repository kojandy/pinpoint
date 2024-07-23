package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.profiler.context.ErrorInfo;

public class StringErrorInfo implements ErrorInfo<String> {
    private final int category;
    private final String content;

    public StringErrorInfo(int category, String content) {
        this.category = category;
        this.content = content;
    }

    @Override
    public int getCategory() {
        return category;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "StringErrorInfo{" +
                "category=" + category +
                ", content='" + content + '\'' +
                '}';
    }
}
