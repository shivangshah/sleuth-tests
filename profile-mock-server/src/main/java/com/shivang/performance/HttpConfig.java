package com.shivang.performance;

import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Primary;

@Configuration
public class HttpConfig {

    @Bean
    public Sampler defaultSampler() {
        return new AlwaysSampler();
    }

    @Bean
    @Primary
    public SpanInjector<HttpServletResponse> customHttpServletResponseSpanInjector() {
        return new CustomHttpServletResponseSpanInjector();
    }

    @Bean
    @Primary
    public SpanExtractor<HttpServletRequest> customHttpServletRequestSpanExtractor(Random random) {
        return new CustomHttpServletRequestSpanExtractor(random);
    }
}
