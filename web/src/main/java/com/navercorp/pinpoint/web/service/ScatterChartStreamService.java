package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.scatter.ScatterData;
import com.navercorp.pinpoint.web.scatter.ScatterDataBuilder;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.scatter.Dot;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@Service
public class ScatterChartStreamService {
    private final ApplicationTraceIndexDao applicationTraceIndexDao;

    public ScatterChartStreamService(ApplicationTraceIndexDao applicationTraceIndexDao) {
        this.applicationTraceIndexDao = Objects.requireNonNull(applicationTraceIndexDao, "ApplicationTraceIndexDao");
    }

    public Flux<ScatterData> selectScatterData(String applicationName, Range range, int xGroupUnit, int yGroupUnit, int limit, boolean backwardDirection) {
        return Flux.create(sink -> {
            Range remainingRange = range;
        });
    }

    private ScatterData selectPartialScatterData(String applicationName, Range range, int xGroupUnit, int yGroupUnit, int limit, boolean backwardDirection) {
        LimitedScanResult<List<Dot>> scanResult = applicationTraceIndexDao.scanTraceScatterData(applicationName, range, limit, backwardDirection);

        ScatterDataBuilder builder = new ScatterDataBuilder(range.getFrom(), range.getTo(), xGroupUnit, yGroupUnit);
        builder.addDot(scanResult.scanData());
        return builder.build();
    }
}
