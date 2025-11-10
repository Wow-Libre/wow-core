package com.register.wowlibre.infrastructure.config;

import org.springframework.boot.actuate.autoconfigure.metrics.export.datadog.DatadogMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration to conditionally exclude Datadog metrics auto-configuration
 * when Datadog is disabled. This prevents Spring Boot from trying to create
 * the DatadogMeterRegistry bean when the API key is not configured or when
 * Datadog is explicitly disabled.
 * 
 * Note: The actual exclusion is handled via spring.autoconfigure.exclude
 * in application.yml, but this class serves as documentation and can be
 * used for additional conditional configuration if needed.
 */
@Configuration
@ConditionalOnProperty(
    name = "management.metrics.export.datadog.enabled",
    havingValue = "false",
    matchIfMissing = true
)
public class DatadogMetricsConfiguration {
    // This configuration is active when Datadog is disabled
    // The exclusion is handled in application.yml via spring.autoconfigure.exclude
}

