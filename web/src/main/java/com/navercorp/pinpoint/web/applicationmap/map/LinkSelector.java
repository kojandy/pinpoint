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
 *
 */

package com.navercorp.pinpoint.web.applicationmap.map;

import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkDataDuplexMap;
import com.navercorp.pinpoint.web.vo.Application;

import java.util.List;

/**
 * @author emeroad
 */
public interface LinkSelector {
    LinkDataDuplexMap select(List<Application> sourceApplications, TimeWindow timeWindow, int outSearchDepth, int inSearchDepth);

    LinkDataDuplexMap select(List<Application> sourceApplications, TimeWindow timeWindow, int outSearchDepth, int inSearchDepth, boolean timeAggregated);
}
