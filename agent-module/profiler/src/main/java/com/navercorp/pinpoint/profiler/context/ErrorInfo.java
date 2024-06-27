package com.navercorp.pinpoint.profiler.context;

public interface ErrorInfo<T> {
    int getCategory();

    T getContent();
}
