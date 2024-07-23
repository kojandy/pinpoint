package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.profiler.context.ErrorInfo;

public class IntErrorInfo implements ErrorInfo<Integer> {
    private final int category;
    private final int content;

    public IntErrorInfo(int category, int content) {
        this.category = category;
        this.content = content;
    }

    @Override
    public int getCategory() {
        return category;
    }

    @Override
    public Integer getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "IntErrorInfo{" +
                "category=" + category +
                ", content=" + content +
                '}';
    }
}
