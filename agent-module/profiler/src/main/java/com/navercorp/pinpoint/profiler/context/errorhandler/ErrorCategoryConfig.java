package com.navercorp.pinpoint.profiler.context.errorhandler;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.ErrorCategory;
import com.navercorp.pinpoint.common.util.StringUtils;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ErrorCategoryConfig {
    private static final String ERROR_MARK_PROPERTY = "profiler.error.mark";
    private static final String DEFAULT_ERROR_MARK = "exception,http-status";
    private static final String ENABLE_ALL = "all";
    private static final String ENABLE_NONE = "none";
    
    private final Map<ErrorCategory, Boolean> categoryStates;
    
    public ErrorCategoryConfig(ProfilerConfig config) {
        Objects.requireNonNull(config, "config");
        
        this.categoryStates = new EnumMap<>(ErrorCategory.class);
        initializeCategoryStates(config);
    }
    
    private void initializeCategoryStates(ProfilerConfig config) {
        String errorMarkValue = config.readString(ERROR_MARK_PROPERTY, DEFAULT_ERROR_MARK);
        
        if (StringUtils.isEmpty(errorMarkValue)) {
            errorMarkValue = DEFAULT_ERROR_MARK;
        }
        
        errorMarkValue = errorMarkValue.trim().toLowerCase();
        
        for (ErrorCategory category : ErrorCategory.values()) {
            categoryStates.put(category, false);
        }
        
        if (ENABLE_ALL.equals(errorMarkValue)) {
            // Enable all categories
            for (ErrorCategory category : ErrorCategory.values()) {
                categoryStates.put(category, true);
            }
        } else if (ENABLE_NONE.equals(errorMarkValue)) {
            // All categories remain disabled (already initialized to false)
        } else {
            // Parse comma-separated category names
            Set<String> enabledCategories = parseEnabledCategories(errorMarkValue);
            
            for (ErrorCategory category : ErrorCategory.values()) {
                boolean enabled = enabledCategories.contains(category.getConfigKey());
                categoryStates.put(category, enabled);
            }
        }
    }
    
    private Set<String> parseEnabledCategories(String errorMarkValue) {
        Set<String> enabledCategories = new HashSet<>();
        
        String[] categories = errorMarkValue.split(",");
        for (String category : categories) {
            String trimmedCategory = category.trim();
            if (!trimmedCategory.isEmpty()) {
                enabledCategories.add(trimmedCategory);
            }
        }
        
        return enabledCategories;
    }
    
    public boolean isEnabled(ErrorCategory category) {
        Objects.requireNonNull(category, "category");
        return categoryStates.getOrDefault(category, false);
    }
    
    public void setEnabled(ErrorCategory category, boolean enabled) {
        Objects.requireNonNull(category, "category");
        categoryStates.put(category, enabled);
    }
    
    public Map<ErrorCategory, Boolean> getAllCategoryStates() {
        return new EnumMap<>(categoryStates);
    }
    
    @Override
    public String toString() {
        return "ErrorCategoryConfig{" +
                "categoryStates=" + categoryStates +
                '}';
    }
}