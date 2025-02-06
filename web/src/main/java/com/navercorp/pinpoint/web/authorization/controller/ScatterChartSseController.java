package com.navercorp.pinpoint.web.authorization.controller;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.scatter.ScatterChartSubscription;
import com.navercorp.pinpoint.web.service.ScatterChartStreamService;
import jakarta.validation.constraints.Positive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

// TODO: implement CustomScatterChartController
@RestController
@RequestMapping("/api/scatter")
@Validated
public class ScatterChartSseController {
    private static final Logger logger = LogManager.getLogger(ScatterChartSseController.class);

    private final ScatterChartStreamService service;

    public ScatterChartSseController(ScatterChartStreamService service) {
        this.service = Objects.requireNonNull(service, "ScatterChartStreamService");
    }

    @GetMapping
    public SseEmitter streamScatterData(
            @RequestParam("application") String applicationName,
            @RequestParam("from") long from,
            @RequestParam("to") long to,
            @RequestParam("xGroupUnit") @Positive int xGroupUnit,
            @RequestParam("yGroupUnit") @Positive int yGroupUnit,
            @RequestParam(value = "backwardDirection", defaultValue = "true") boolean backwardDirection,
            @RequestParam(value = "filter", required = false) String filter
    ) {
        // TODO: validate range with RangeValidator
        final Range range = Range.between(from, to);
        logger.debug("stream scatter data: application={}, range={}, backwardDirection={}, filter={}.", applicationName, range, backwardDirection, filter);

        // TODO: pull timeout value from configuration
        SseEmitter sseEmitter = new SseEmitter(10000L);
        // TODO: implement filtered scatter data
        service.subscribe(new ScatterChartSubscription(sseEmitter, applicationName, range, xGroupUnit, yGroupUnit, backwardDirection));
        return sseEmitter;
    }
}
