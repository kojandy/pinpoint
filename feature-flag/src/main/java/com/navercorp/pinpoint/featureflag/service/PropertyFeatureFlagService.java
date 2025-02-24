package com.navercorp.pinpoint.featureflag.service;

import org.springframework.stereotype.Service;

@Service
public class PropertyFeatureFlagService implements FeatureFlagService {
    public boolean isEnabled(String featureName, String applicationName) {
        return true;
    }
}
