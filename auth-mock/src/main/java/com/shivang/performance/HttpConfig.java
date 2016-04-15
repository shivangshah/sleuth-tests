package com.shivang.performance;

import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;

@Configuration
public class HttpConfig {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 500;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 500;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    @Bean
    public Sampler defaultSampler() {
        return new AlwaysSampler();
    }

    @Bean
    @Primary
    public SpanInjector<HttpRequest> customHttpServletResponseSpanInjector() {
        return new CustomHttpServletResponseSpanInjector();
    }

    @Bean
    @Primary
    public SpanExtractor<HttpServletRequest> customHttpServletRequestSpanExtractor(Random random) {
        return new CustomHttpServletRequestSpanExtractor(random);
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager
                .setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();

        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return defaultHttpClient;
    }

    @Bean
    public AsyncClientHttpRequestFactory asyncHttpRequestFactory() {
        return new HttpComponentsAsyncClientHttpRequestFactory(
                asyncHttpClient());
    }

    @Bean
    public CloseableHttpAsyncClient asyncHttpClient() {
        try {
            PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
            connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
            connectionManager
                    .setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                    .build();

            CloseableHttpAsyncClient httpclient = HttpAsyncClientBuilder
                    .create().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(config).build();
            return httpclient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
