/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.web.applicationmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.web.applicationmap.histogram.TimeHistogramFormat;

import java.util.Objects;

/**
 * @author emeroad
 */
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class MapView {
    private final ApplicationMap applicationMap;
    private final TimeWindow timeWindow;
    private final Class<?> activeView;
    private final TimeHistogramFormat timeHistogramFormat;

    public MapView(ApplicationMap applicationMap, Class<?> activeView, final TimeHistogramFormat timeHistogramFormat) {
        this.applicationMap = applicationMap;
        this.timeWindow = null;
        this.activeView = Objects.requireNonNull(activeView, "activeView");
        this.timeHistogramFormat = Objects.requireNonNull(timeHistogramFormat, "timeHistogramFormat");
    }

    public MapView(ApplicationMap applicationMap, TimeWindow timeWindow, Class<?> activeView, final TimeHistogramFormat timeHistogramFormat) {
        this.applicationMap = applicationMap;
        this.timeWindow = Objects.requireNonNull(timeWindow, "timeWindow");
        this.activeView = Objects.requireNonNull(activeView, "activeView");
        this.timeHistogramFormat = Objects.requireNonNull(timeHistogramFormat, "timeHistogramFormat");
    }

    @JsonProperty("applicationMapData")
    public ApplicationMapView getApplicationMap() {
        if (timeWindow == null) {
            return new ApplicationMapView(this.applicationMap, activeView, timeHistogramFormat);
        }
        return new ApplicationMapView(this.applicationMap, timeWindow, activeView, timeHistogramFormat);
    }
}
