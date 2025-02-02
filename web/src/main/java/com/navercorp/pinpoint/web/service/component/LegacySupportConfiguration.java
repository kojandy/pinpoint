package com.navercorp.pinpoint.web.service.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LegacySupportConfiguration {
    @Bean
    public LegacyAgentCompatibility legacyAgentCompatibility(@Value("${pinpoint.web.agent-status.legacy-agent-support:true}")
                                                             boolean legacyAgentSupport) {
        if (legacyAgentSupport) {
            return new DefaultLegacyAgentCompatibility();
        }
        return new DisableAgentCompatibility();
    }
}
