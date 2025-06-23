package com.navercorp.pinpoint.bootstrap.context;

public enum ErrorCategory {
    HTTP_STATUS_CODE("http-status", "HTTP Status Code Errors"),
    SQL_COUNT_EXCEEDED("sql-count", "SQL Count Exceeded Errors"),
    EXCEPTION("exception", "Exception Based Errors"),
    CUSTOM("custom", "Custom Errors");
    
    private final String configKey;
    private final String description;
    
    ErrorCategory(String configKey, String description) {
        this.configKey = configKey;
        this.description = description;
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ErrorCategory fromConfigKey(String configKey) {
        for (ErrorCategory category : values()) {
            if (category.configKey.equals(configKey)) {
                return category;
            }
        }
        return null;
    }
}