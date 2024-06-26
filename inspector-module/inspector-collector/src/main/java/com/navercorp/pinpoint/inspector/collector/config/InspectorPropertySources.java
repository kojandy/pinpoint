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

package com.navercorp.pinpoint.inspector.collector.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author minwoo.jung
 */

@PropertySources({
        @PropertySource(name = "InspectorPropertySources-KAFKA", value = { InspectorPropertySources.KAFKA_TOPIC, InspectorPropertySources.COLLECTOR_CONFIG}),
})
public class InspectorPropertySources {
    public static final String KAFKA_TOPIC = "classpath:inspector/collector/kafka-topic-inspector.properties";

    public static final String COLLECTOR_CONFIG = "classpath:inspector/collector/profiles/${pinpoint.profiles.active:release}/inspector-collector.properties";
}
