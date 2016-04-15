package com.shivang.performance;

import java.util.Random;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.cloud.sleuth.trace.CustomTracer;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(value = "spring.sleuth.enabled", matchIfMissing = true)
@EnableConfigurationProperties
public class CustomTraceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    @Primary
    public DefaultTracer defaultTracer(Sampler sampler, Random random,
            SpanNamer spanNamer, SpanLogger spanLogger,
            SpanReporter spanReporter) {
        return new CustomTracer(sampler, random, spanNamer, spanLogger,
                spanReporter);
    }
}
