package com.github.walterfan.util.ratelimit;

import com.codahale.metrics.MetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Walter Fan
 * @Date: 26/5/2019, Sun
 **/
@Configuration
public class MetricsConfig {
    @Bean
    public MetricRegistry metricsRegistry() {
        return new MetricRegistry();
    }
}

