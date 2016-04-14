package com.shivang.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

@RestController
public class PingController {

    @Autowired
    private TaskServiceImpl taskService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    @RequestMapping("/async")
    public DeferredResult<Map<String, Boolean>> async() {
        LOGGER.warn("calling /async on thread: " + Thread.currentThread().getName());
        DeferredResult<Map<String, Boolean>> deferredResult = new DeferredResult<>();
        Observable<ResponseEntity<HashMap>> result = taskService.asyncCall();
        result.subscribeOn(Schedulers.computation()).subscribe(hashMapResponseEntity -> {
            LOGGER.warn("calling /async subscribe on thread: " + Thread.currentThread().getName());
            deferredResult.setResult(hashMapResponseEntity.getBody());
        });
        LOGGER.warn("getting out /async on thread: " + Thread.currentThread().getName());
        return deferredResult;
    }

    @RequestMapping("/sync")
    public DeferredResult<Map<String, Boolean>> sync() {
        LOGGER.warn("calling /sync on thread: " + Thread.currentThread().getName());
        DeferredResult<Map<String, Boolean>> deferredResult = new DeferredResult<>();
        Observable<ResponseEntity<HashMap>> result = taskService.syncCall();
        result.subscribeOn(Schedulers.computation()).subscribe(hashMapResponseEntity -> {
            LOGGER.warn("calling /sync subscribe on thread: " + Thread.currentThread().getName());
            deferredResult.setResult(hashMapResponseEntity.getBody());
        });
        return deferredResult;
    }
}
