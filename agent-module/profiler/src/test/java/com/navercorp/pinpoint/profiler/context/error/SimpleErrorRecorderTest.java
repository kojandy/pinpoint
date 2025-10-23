package com.navercorp.pinpoint.profiler.context.error;

import com.navercorp.pinpoint.common.trace.ErrorCategory;
import com.navercorp.pinpoint.profiler.context.id.LocalTraceRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimpleErrorRecorderTest {
    private SimpleErrorRecorder sut;

    @Mock
    LocalTraceRoot localTraceRoot;

    @BeforeEach
    void setUp() {
        sut = new SimpleErrorRecorder(localTraceRoot);
    }

    void test() {
        sut.recordError(ErrorCategory.UNKNOWN);

        verify(localTraceRoot).getShared()
    }
}