package com.navercorp.pinpoint.web.scatter;

import com.navercorp.pinpoint.common.server.util.time.Range;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

public record ScatterChartSubscription(
        SseEmitter emitter,
        String applicationName,
        Range range,
        int xGroupUnit,
        int yGroupUnit,
        boolean backwardDirection
) {
    public ScatterChartSubscription {
        Objects.requireNonNull(emitter);
        Objects.requireNonNull(applicationName);
        Objects.requireNonNull(range);
        if (xGroupUnit <= 0) {
            throw new IllegalArgumentException("xGroupUnit must be positive");
        }
        if (yGroupUnit <= 0) {
            throw new IllegalArgumentException("yGroupUnit must be positive");
        }
    }
}
