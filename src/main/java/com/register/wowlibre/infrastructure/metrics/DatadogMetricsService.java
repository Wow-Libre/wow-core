package com.register.wowlibre.infrastructure.metrics;

/**
 * Service interface for sending metrics to Datadog
 */
public interface DatadogMetricsService {
    
    /**
     * Records a metric execution for a repository method
     * 
     * @param methodName The name of the method that was executed
     * @param transactionId The transaction ID for tracking
     * @param additionalTags Additional tags to include in the metric (optional)
     */
    void recordMethodExecution(String methodName, String transactionId, String... additionalTags);
    
    /**
     * Records a metric with a specific value
     * 
     * @param metricName The name of the metric
     * @param value The value to record
     * @param transactionId The transaction ID for tracking
     * @param tags Additional tags to include in the metric
     */
    void recordMetric(String metricName, double value, String transactionId, String... tags);
}

