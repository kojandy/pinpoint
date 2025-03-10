/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.bootstrap.config;

import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcOption;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.common.config.util.PlaceHolder;
import com.navercorp.pinpoint.common.config.util.spring.PropertyPlaceholderHelper;
import com.navercorp.pinpoint.common.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author emeroad
 * @author netspider
 */
public class DefaultProfilerConfig implements ProfilerConfig {
    public static final String AGENT_ROOT_PATH_KEY = "pinpoint.agent.root.path";

    // TestAgent only
    public static final String IMPORT_PLUGIN = "profiler.plugin.import-plugin";

    private final PropertyPlaceholderHelper placeholder;

    private final Properties properties;

    private final JdbcOption jdbcOption;

    public DefaultProfilerConfig() {
        this.properties = new Properties();
        this.jdbcOption = JdbcOption.empty();
        this.placeholder = newPlaceholder(false);
    }

    DefaultProfilerConfig(Properties properties, JdbcOption jdbcOption) {
        this.properties = Objects.requireNonNull(properties, "properties");
        this.jdbcOption = Objects.requireNonNull(jdbcOption, "jdbcOption");

        this.placeholder = newPlaceholder(false);
    }

    private PropertyPlaceholderHelper newPlaceholder(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(PlaceHolder.START, PlaceHolder.END, PlaceHolder.DELIMITER, ignoreUnresolvablePlaceholders);
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public JdbcOption getJdbcOption() {
        return jdbcOption;
    }

    //
//    @Override
//    public String getActiveProfile() {
//        return activeProfile;
//    }
//
//    @Override
//    public int getJdbcSqlCacheSize() {
//        return jdbcSqlCacheSize;
//    }
//
//    @Override
//    public boolean isTraceSqlBindValue() {
//        return traceSqlBindValue;
//    }
//
//    @Override
//    public int getMaxSqlBindValueSize() {
//        return maxSqlBindValueSize;
//    }
//
//    @Override
//    public int getMaxSqlCacheLength() {
//        return maxSqlCacheLength;
//    }
//
//    @Override
//    public int getMaxSqlLength() {
//        return maxSqlLength;
//    }
//
//    @Override
//    public String getGrpcStatLoggingPeriod() {
//        return grpcStatLoggingPeriod;
//    }
//
//    @Override
//    public HttpStatusCodeErrors getHttpStatusCodeErrors() {
//        return httpStatusCodeErrors;
//    }
//
//    @Value("${profiler.http.status.code.errors}")
//    void setHttpStatusCodeErrors(String httpStatusCodeErrors) {
//        List<String> httpStatusCodeErrorList = StringUtils.tokenizeToStringList(httpStatusCodeErrors, ",");
//        this.httpStatusCodeErrors = new HttpStatusCodeErrors(httpStatusCodeErrorList);
//    }
//
//    @Override
//    public String getInjectionModuleFactoryClazzName() {
//        return injectionModuleFactoryClazzName;
//    }
//
//    @Override
//    public String getApplicationNamespace() {
//        return applicationNamespace;
//    }
//
//    @Override
//    public String getAgentRootPath() {
//        return agentRootPath;
//    }


    @Override
    public String readString(String propertyName) {
        return readString(propertyName, null);
    }

    @Override
    public String readString(String propertyName, String defaultValue) {
        return getProperty(propertyName, defaultValue);
    }

    private String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    private String getProperty(String propertyName, String defaultValue) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            return defaultValue;
        }
        property = placeholder.replacePlaceholders(property, properties);
        if (property == null) {
            return defaultValue;
        }
        return property;
    }

    @Override
    public int readInt(String propertyName, int defaultValue) {
        final String value = getProperty(propertyName);
        if (value == null) {
            return defaultValue;
        }
        return NumberUtils.parseInteger(value, defaultValue);
    }


    @Override
    public long readLong(String propertyName, long defaultValue) {
        final String value = getProperty(propertyName);
        if (value == null) {
            return defaultValue;
        }
        return NumberUtils.parseLong(value, defaultValue);
    }

    @Override
    public List<String> readList(String propertyName) {
        final String value = getProperty(propertyName);
        if (StringUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        return StringUtils.tokenizeToStringList(value, ",");
    }

    @Override
    public boolean readBoolean(String propertyName, boolean defaultValue) {
        final String value = getProperty(propertyName);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public Map<String, String> readPattern(String propertyNamePatternRegex) {
        final Pattern pattern = Pattern.compile(propertyNamePatternRegex);
        final Map<String, String> result = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (pattern.matcher(key).matches()) {
                String value = getProperty(key);
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "DefaultProfilerConfig{" +
                "properties=" + properties +
                '}';
    }
}
