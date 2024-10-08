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

package com.navercorp.pinpoint.collector.monitor;

import com.navercorp.pinpoint.collector.monitor.receiver.ExecutorFactoryBean;
import com.navercorp.pinpoint.common.server.executor.ExecutorCustomizer;
import com.navercorp.pinpoint.common.server.thread.MonitoringExecutorProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.Objects;

public class MonitoringExecutors {

    private final ExecutorCustomizer<ThreadPoolExecutorFactoryBean> customizer;
    private final MonitoredThreadPoolExecutorFactoryProvider provider;

    public MonitoringExecutors(
            @Qualifier("collectorExecutorCustomizer")
            ExecutorCustomizer<ThreadPoolExecutorFactoryBean> customizer,
            MonitoredThreadPoolExecutorFactoryProvider provider
    ) {
        this.customizer = Objects.requireNonNull(customizer, "customizer");
        this.provider = provider;
    }

    public ThreadPoolExecutorFactoryBean newExecutorFactoryBean(MonitoringExecutorProperties properties, String beanName) {
        MonitoredThreadPoolExecutorFactory factory = null;
        if (provider != null) {
            factory = provider.newFactory(beanName, properties);
        }
        ExecutorFactoryBean executor = new ExecutorFactoryBean();
        executor.setExecutorFactory(factory);

        customizer.customize(executor, properties);
        if (properties.getThreadNamePrefix() == null) {
            executor.setThreadNamePrefix(beanName);
        }
        return executor;
    }
}
