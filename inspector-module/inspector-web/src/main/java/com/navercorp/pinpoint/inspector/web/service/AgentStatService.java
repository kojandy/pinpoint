/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.inspector.web.service;

import com.navercorp.pinpoint.common.timeseries.point.DataPoint;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.inspector.web.model.InspectorDataSearchKey;
import com.navercorp.pinpoint.inspector.web.model.InspectorMetricData;
import com.navercorp.pinpoint.inspector.web.model.InspectorMetricGroupData;

import java.util.List;

/**
 * @author minwoo.jung
 */
public interface AgentStatService {
    InspectorMetricData selectAgentStat(InspectorDataSearchKey inspectorDataSearchKey, TimeWindow timeWindow);

    List<DataPoint<Double>> selectAgentStatUnconvertedTime(InspectorDataSearchKey inspectorDataSearchKey, TimeWindow timeWindow);

    InspectorMetricGroupData selectAgentStatWithGrouping(InspectorDataSearchKey inspectorDataSearchKey, TimeWindow timeWindow);
}
