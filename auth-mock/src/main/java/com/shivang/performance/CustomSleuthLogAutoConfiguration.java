package com.shivang.performance;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.log.NoOpSpanLogger;
import org.springframework.cloud.sleuth.log.Slf4jSpanLogger;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(value = "spring.sleuth.enabled", matchIfMissing = true)
public class CustomSleuthLogAutoConfiguration {

    @Configuration
    @ConditionalOnClass(MDC.class)
    protected static class CustomSlf4jConfiguration {

        /**
         * Name pattern for which span should not be printed in the logs
         */
        @Value("${spring.sleuth.log.slf4j.nameSkipPattern:}")
        private String nameSkipPattern;

        @Bean
        @ConditionalOnProperty(value = "spring.sleuth.log.slf4j.enabled", matchIfMissing = true)
        @Primary
        public SpanLogger slf4jSpanLogger() {
            // Sets up MDC entries X-B3-TraceId and X-B3-SpanId
            return new CustomSlf4jSpanLogger(this.nameSkipPattern);
        }

        @Bean
        @ConditionalOnProperty(value = "spring.sleuth.log.slf4j.enabled", havingValue = "false")
        @ConditionalOnMissingBean
        public SpanLogger noOpSlf4jSpanLogger() {
            return new NoOpSpanLogger();
        }
    }

    @Bean
    @ConditionalOnMissingClass("org.slf4j.MDC")
    @ConditionalOnMissingBean
    public SpanLogger defaultLoggedSpansHandler() {
        return new NoOpSpanLogger();
    }
}
