package com.register.wowlibre.infrastructure.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock implementation of DatadogMetricsService for testing and development.
 * Simulates sending metrics to Datadog by logging and storing them in memory.
 */
@Service
public class MockDatadogMetricsService implements DatadogMetricsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MockDatadogMetricsService.class);
    
    // In-memory storage to track metrics (for verification purposes)
    private final Map<String, List<MetricRecord>> metricsHistory = new ConcurrentHashMap<>();
    private final AtomicLong metricCounter = new AtomicLong(0);
    
    @Override
    public void recordMethodExecution(String methodName, String transactionId, String... additionalTags) {
        long metricId = metricCounter.incrementAndGet();
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Build metric name following Datadog conventions
        String metricName = String.format("wowlibre.repository.%s.execution", methodName);
        
        // Build tags
        List<String> tags = new ArrayList<>();
        tags.add("transaction_id:" + transactionId);
        tags.add("method:" + methodName);
        tags.add("component:JpaAccountGameAdapter");
        if (additionalTags != null && additionalTags.length > 0) {
            Collections.addAll(tags, additionalTags);
        }
        
        // Store metric record
        MetricRecord record = new MetricRecord(metricId, metricName, 1.0, timestamp, transactionId, methodName, tags);
        metricsHistory.computeIfAbsent(methodName, k -> new ArrayList<>()).add(record);
        
        // Simulate sending to Datadog (log it)
        LOGGER.info("[MockDatadogMetrics] Metric sent to Datadog - metricName: {}, transactionId: {}, tags: {}, timestamp: {}",
                metricName, transactionId, String.join(", ", tags), timestamp);
        
        // Log in Datadog format for easy verification
        LOGGER.debug("[Datadog] {}:1|g|#{}", metricName, String.join(",", tags));
    }
    
    @Override
    public void recordMetric(String metricName, double value, String transactionId, String... tags) {
        long metricId = metricCounter.incrementAndGet();
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Build full tags list
        List<String> fullTags = new ArrayList<>();
        fullTags.add("transaction_id:" + transactionId);
        if (tags != null && tags.length > 0) {
            Collections.addAll(fullTags, tags);
        }
        
        // Store metric record
        MetricRecord record = new MetricRecord(metricId, metricName, value, timestamp, transactionId, null, fullTags);
        metricsHistory.computeIfAbsent(metricName, k -> new ArrayList<>()).add(record);
        
        // Simulate sending to Datadog (log it)
        LOGGER.info("[MockDatadogMetrics] Metric sent to Datadog - metricName: {}, value: {}, transactionId: {}, tags: {}, timestamp: {}",
                metricName, value, transactionId, String.join(", ", fullTags), timestamp);
        
        // Log in Datadog format
        LOGGER.debug("[Datadog] {}:{}|g|#{}", metricName, value, String.join(",", fullTags));
    }
    
    /**
     * Get metrics history for a specific method (useful for testing/verification)
     */
    public List<MetricRecord> getMetricsForMethod(String methodName) {
        return metricsHistory.getOrDefault(methodName, Collections.emptyList());
    }
    
    /**
     * Get all metrics history
     */
    public Map<String, List<MetricRecord>> getAllMetrics() {
        return new HashMap<>(metricsHistory);
    }
    
    /**
     * Get total number of metrics recorded
     */
    public long getTotalMetricsCount() {
        return metricCounter.get();
    }
    
    /**
     * Clear all metrics history (useful for testing)
     */
    public void clearMetrics() {
        metricsHistory.clear();
        metricCounter.set(0);
    }
    
    /**
     * Verify if a method was executed for a given transaction ID
     */
    public boolean wasMethodExecuted(String methodName, String transactionId) {
        return metricsHistory.getOrDefault(methodName, Collections.emptyList())
                .stream()
                .anyMatch(record -> transactionId.equals(record.getTransactionId()));
    }
    
    /**
     * Internal class to store metric records
     */
    public static class MetricRecord {
        private final long id;
        private final String metricName;
        private final double value;
        private final LocalDateTime timestamp;
        private final String transactionId;
        private final String methodName;
        private final List<String> tags;
        
        public MetricRecord(long id, String metricName, double value, LocalDateTime timestamp,
                           String transactionId, String methodName, List<String> tags) {
            this.id = id;
            this.metricName = metricName;
            this.value = value;
            this.timestamp = timestamp;
            this.transactionId = transactionId;
            this.methodName = methodName;
            this.tags = new ArrayList<>(tags);
        }
        
        public long getId() { return id; }
        public String getMetricName() { return metricName; }
        public double getValue() { return value; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getTransactionId() { return transactionId; }
        public String getMethodName() { return methodName; }
        public List<String> getTags() { return new ArrayList<>(tags); }
        
        @Override
        public String toString() {
            return String.format("MetricRecord{id=%d, metricName='%s', value=%.2f, transactionId='%s', methodName='%s', timestamp=%s}",
                    id, metricName, value, transactionId, methodName, timestamp);
        }
    }
}

