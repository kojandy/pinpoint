package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.scatter.ScatterChartSubscription;
import com.navercorp.pinpoint.web.scatter.ScatterData;
import com.navercorp.pinpoint.web.scatter.ScatterDataBuilder;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.scatter.Dot;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ScatterChartStreamService {
    // TODO: pull chunk size from configuration
    private static final int CHUNK_SIZE = 1000;

    private final ApplicationTraceIndexDao applicationTraceIndexDao;

    public ScatterChartStreamService(ApplicationTraceIndexDao applicationTraceIndexDao) {
        this.applicationTraceIndexDao = Objects.requireNonNull(applicationTraceIndexDao, "ApplicationTraceIndexDao");
    }

    // TODO: test async
    // @Async
    public void subscribe(ScatterChartSubscription sub) {
        fetchScatterData(sub.applicationName(), sub.range(), sub.xGroupUnit(), sub.yGroupUnit(), sub.backwardDirection())
                .doOnNext(data -> {
                    try {
                        sub.emitter().send(
                                ServerSentEvent.builder(data)
                                        .event("scatter"),
                                MediaType.APPLICATION_JSON
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnError(sub.emitter()::completeWithError)
                .doOnComplete(sub.emitter()::complete)
                .subscribe();
    }

    private Flux<ScatterData> fetchScatterData(String applicationName, Range range, int xGroupUnit, int yGroupUnit, boolean backwardDirection) {
        return Flux.create(sink -> {
            Range remainingRange = range;
            // TODO: check if this completes
            do {
                ScatterData scatterData = fetchChunkedScatterData(applicationName, remainingRange, xGroupUnit, yGroupUnit, CHUNK_SIZE, backwardDirection);
                sink.next(scatterData);
                if (backwardDirection) {
                    remainingRange = Range.unchecked(remainingRange.getFrom(), scatterData.getOldestAcceptedTime() - 1);
                } else {
                    remainingRange = Range.unchecked(scatterData.getLatestAcceptedTime() + 1, remainingRange.getTo());
                }
                if (scatterData.getDotSize() < CHUNK_SIZE) sink.complete();
            } while (true);
        });
    }

    private ScatterData fetchChunkedScatterData(String applicationName, Range range, int xGroupUnit, int yGroupUnit, int chunkSize, boolean backwardDirection) {
        LimitedScanResult<List<Dot>> scanResult = applicationTraceIndexDao.scanTraceScatterData(applicationName, range, chunkSize, backwardDirection);

        ScatterDataBuilder builder = new ScatterDataBuilder(range.getFrom(), range.getTo(), xGroupUnit, yGroupUnit);
        builder.addDot(scanResult.scanData());
        return builder.build();
    }
}
