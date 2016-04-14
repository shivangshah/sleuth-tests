package com.shivang.performance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class PingController {

    @Autowired
    private TaskServiceImpl taskService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    @RequestMapping("/ping")
    public DeferredResult<Map<String, Boolean>> ping() {
        DeferredResult<Map<String, Boolean>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(taskService::execute)
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
        LOGGER.warn("Servlet thread released");

        return deferredResult;
    }
}