/*
 * Copyright 2025 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.inspector.web.service;

import com.navercorp.pinpoint.common.timeseries.point.DataPoint;
import com.navercorp.pinpoint.common.timeseries.time.Range;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindowSampler;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindowSlotCentricSampler;
import com.navercorp.pinpoint.inspector.web.model.InspectorDataSearchKey;
import com.navercorp.pinpoint.pinot.tenant.TenantProvider;
import com.navercorp.pinpoint.web.service.stat.AgentWarningStatService;
import com.navercorp.pinpoint.web.vo.timeline.inspector.AgentState;
import com.navercorp.pinpoint.web.vo.timeline.inspector.AgentStatusTimelineSegment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author minwoo-jung
 */
@Service
public class AgentWarningStatServiceImpl implements AgentWarningStatService {

    private static final String DEADLOCK_DEFINITION_ID = "deadlock";
    private static final TimeWindowSampler DEFAULT_TIME_WINDOW_SAMPLER = new TimeWindowSlotCentricSampler(10000L, 1000000);
    private static final long LIMIT_TIME = 60000;

    private final AgentStatService agentStatService;
    private final TenantProvider tenantProvider;

    public AgentWarningStatServiceImpl(AgentStatService agentStatService, TenantProvider tenantProvider) {
        this.agentStatService = Objects.requireNonNull(agentStatService, "agentStatService");
        this.tenantProvider = Objects.requireNonNull(tenantProvider, "tenantProvider");

    }

    @Override
    public List<AgentStatusTimelineSegment> select(String applicationName, String agentId, Range range) {
        String tenantId = tenantProvider.getTenantId();
        TimeWindow timeWindow = new TimeWindow(range, DEFAULT_TIME_WINDOW_SAMPLER);
        InspectorDataSearchKey inspectorDataSearchKey = new InspectorDataSearchKey(tenantId, applicationName, agentId, DEADLOCK_DEFINITION_ID, timeWindow);
        List<DataPoint<Double>> dataPoints = agentStatService.selectAgentStatUnconvertedTime(inspectorDataSearchKey, timeWindow);
        return createTimelineSegment(dataPoints);
    }

    private List<AgentStatusTimelineSegment> createTimelineSegment(List<DataPoint<Double>> metricDataList) {
        if (CollectionUtils.isEmpty(metricDataList)) {
            return Collections.emptyList();
        }

        List<AgentStatusTimelineSegment> timelineSegmentList = new ArrayList<>(metricDataList.size());
        long beforeTimestamp = -1;
        int index = 0;

        for (int i = 0; i < metricDataList.size(); i++) {
            DataPoint<Double> metricData = metricDataList.get(i);
            if (i == 0) {
                beforeTimestamp =  metricData.getTimestamp();
            } else {
                boolean needSeparation =  metricData.getTimestamp() > beforeTimestamp + LIMIT_TIME;

                if (needSeparation) {
                    AgentStatusTimelineSegment timelineSegment = createUnstableTimelineSegment(metricDataList.subList(index, i)); //!!! 여기서 어떻게 하는건지 알아야함.
                    timelineSegmentList.add(timelineSegment);
                    index = i;
                }

                beforeTimestamp =  metricData.getTimestamp();
            }
        }

        AgentStatusTimelineSegment timelineSegment = createUnstableTimelineSegment(metricDataList.subList(index, metricDataList.size()));
        timelineSegmentList.add(timelineSegment);
        return timelineSegmentList;
    }

    private AgentStatusTimelineSegment createUnstableTimelineSegment(List<DataPoint<Double>> metricDataList) {
        if (CollectionUtils.isEmpty(metricDataList)) {
            return null;
        }

        DataPoint<Double> first = CollectionUtils.firstElement(metricDataList);
        DataPoint<Double> last = CollectionUtils.lastElement(metricDataList);

        if (first == null || last == null) {
            return null;
        }

        AgentStatusTimelineSegment timelineSegment = new AgentStatusTimelineSegment();
        timelineSegment.setStartTimestamp(first.getTimestamp());
        timelineSegment.setEndTimestamp(last.getTimestamp());
        timelineSegment.setValue(AgentState.UNSTABLE_RUNNING);
        return timelineSegment;
    }

}
