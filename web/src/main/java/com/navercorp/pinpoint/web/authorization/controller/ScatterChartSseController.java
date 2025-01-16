package com.navercorp.pinpoint.web.authorization.controller;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.scatter.ScatterData;
import com.navercorp.pinpoint.web.service.ScatterChartStreamService;
import com.navercorp.pinpoint.web.util.LimitUtils;
import io.vertx.ext.web.client.WebClient;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@RequestMapping("/api")
@Validated
public class ScatterChartSseController {
    private static final Logger logger = LogManager.getLogger(ScatterChartSseController.class);

    private final ScatterChartStreamService service;

    public ScatterChartSseController(ScatterChartStreamService service) {
        this.service = Objects.requireNonNull(service, "ScatterChartStreamService");
    }

    @GetMapping(value = "/streamScatterData")
    public Flux<ServerSentEvent<ScatterData>> streamScatterData(
            @RequestParam("application") String applicationName,
            @RequestParam("from") long from,
            @RequestParam("to") long to,
            @RequestParam("xGroupUnit") @Positive int xGroupUnit,
            @RequestParam("yGroupUnit") @Positive int yGroupUnit,
            @RequestParam("limit") @PositiveOrZero int limitParam,
            @RequestParam(value = "backwardDirection", defaultValue = "true") boolean backwardDirection
    ) {
        final Range range = Range.between(from, to);
        final int limit = Math.max(limitParam, LimitUtils.MAX);
        logger.debug("stream scatter data. application={}, range={}, limit={}, backwardDirection={}", applicationName, range, limit, backwardDirection);

        return service.selectScatterData(applicationName, range, xGroupUnit, yGroupUnit, limit, backwardDirection)
                .map(data -> ServerSentEvent.builder(data).build());
    }

    @GetMapping("/test")
    public Flux<ServerSentEvent<Integer>> test() {
        return Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(ServerSentEvent.builder(i).build());
            }
            sink.complete();
        });
    }
}
