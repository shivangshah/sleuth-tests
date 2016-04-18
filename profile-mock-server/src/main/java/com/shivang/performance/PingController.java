package com.shivang.performance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;

@RestController
public class PingController {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private Tracer tracer;
    @Autowired
    private TraceKeys traceKeys;
    @Autowired
    private SpanNamer spanNamer;
    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    @RequestMapping("/ping")
    public DeferredResult<Map<String, Boolean>> ping() {
        DeferredResult<Map<String, Boolean>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(() -> taskService.execute(),
                new TraceableExecutorService(Executors.newFixedThreadPool(5), tracer, traceKeys, spanNamer))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
        LOGGER.warn("Servlet thread released");

        return deferredResult;
    }
}
