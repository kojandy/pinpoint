package com.navercorp.pinpoint.featureflag.service;

public interface FeatureFlagService {
    boolean isEnabled(String featureName, String applicationName);

    default boolean isDisabled(String sqlstat, String applicationId) {
        return !isEnabled(sqlstat, applicationId);
    }
}
