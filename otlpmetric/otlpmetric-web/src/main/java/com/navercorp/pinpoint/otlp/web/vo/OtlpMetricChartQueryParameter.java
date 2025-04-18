package com.navercorp.pinpoint.otlp.web.vo;

import com.google.common.primitives.Ints;
import com.navercorp.pinpoint.common.timeseries.window.TimePrecision;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.metric.web.util.QueryParameter;
import com.navercorp.pinpoint.otlp.common.model.DataType;
import com.navercorp.pinpoint.otlp.common.web.definition.property.AggregationFunction;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Deprecated
public class OtlpMetricChartQueryParameter extends QueryParameter {
    private final String serviceName;
    private final String applicationName;
    private final String agentId;
    private final String metricGroupName;
    private final String metricName;
    private final String fieldName;
    private final List<String> tags;
    private final String version;
    private final AggregationFunction aggregationFunction;
    private final int dataType;
    private final TimeWindow timeWindow;

    public DataType getDataType() {
        return DataType.forNumber(dataType);
    }

    protected OtlpMetricChartQueryParameter(Builder builder) {
        super(builder.getRange(), builder.getTimePrecision(), builder.getLimit());
        this.serviceName = builder.serviceName;
        this.applicationName = builder.applicationName;
        this.agentId = builder.agentId;
        this.metricGroupName = builder.metricGroupName;
        this.metricName = builder.metricName;
        this.fieldName = builder.fieldName;
        this.tags = builder.tags;
        this.aggregationFunction = builder.aggregationFunction;
        this.dataType = builder.dataType;
        this.version = builder.version;
        this.timeWindow = builder.timeWindow;
    }

    public static class Builder extends QueryParameter.Builder<Builder> {
        private String serviceName;
        private String applicationName;
        private String agentId;
        private String metricGroupName;
        private String metricName;
        private String fieldName;
        private List<String> tags = List.of();
        private String version;
        private AggregationFunction aggregationFunction;
        private int dataType;
        private TimeWindow timeWindow;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return self();
        }
        public Builder setApplicationName(String applicationName) {
            this.applicationName = applicationName;
            return self();
        }
        public Builder setAgentId(String agentId) {
            this.agentId = agentId;
            return self();
        }
        public Builder setMetricGroupName(String metricGroupName) {
            this.metricGroupName = metricGroupName;
            return self();
        }
        public Builder setMetricName(String metricName) {
            this.metricName = metricName;
            return self();
        }
        public Builder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return self();
        }

        public Builder setTags(List<String> tags) {
            this.tags = tags;
            return self();
        }

        public Builder setVersion(String version) {
            this.version = version;
            return self();
        }

        public Builder setAggregationFunction(AggregationFunction aggregationFunction) {
            this.aggregationFunction = aggregationFunction;
            return self();
        }

        public Builder setDataType(DataType dataType) {
            this.dataType = dataType.getNumber();
            return self();
        }

        public Builder setLimit(int limit) {
            this.limit = Ints.constrainToRange(limit, 50, 200);
            return self();
        }

        public Builder setTimeWindow(TimeWindow timeWindow) {
            this.timeWindow = timeWindow;
            this.range = timeWindow.getWindowRange();
            this.timeSize = timeWindow.getWindowSlotSize();
            this.timePrecision = TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, timeWindow.getWindowSlotSize());
            this.limit = timeWindow.getWindowRangeCount();
            return self();
        }

        @Override
        public OtlpMetricChartQueryParameter build() {
            if (timeWindow == null) {
                throw new InvalidParameterException("TimeWindow is required.");
            }

            return new OtlpMetricChartQueryParameter(this);
        }
    }

    @Override
    public String toString() {
        return "OtlpMetricChartQueryParameter{" +
                "serviceName='" + serviceName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", metricGroupName='" + metricGroupName + '\'' +
                ", metricName='" + metricName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", tags=" + tags +
                ", version='" + version + '\'' +
                ", aggregationFunction=" + aggregationFunction +
                ", dataType=" + dataType +
                ", TimePrecision=" + timePrecision +
                ", range=" + range.prettyToString() +
                ", range(from)=" + range.getFrom() +
                ", range(to)=" + range.getTo() +
                ", limit=" + limit +
                '}';
    }
}
