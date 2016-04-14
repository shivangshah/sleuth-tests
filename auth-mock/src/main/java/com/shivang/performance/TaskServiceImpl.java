package com.shivang.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.HashMap;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TaskServiceImpl {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;
    private AsyncRestTemplate localAsyncRestTemplate = new AsyncRestTemplate();
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private String profileUrl = "http://localhost:8081/ping";

    public Observable<ResponseEntity<HashMap>> asyncCall() {
        LOGGER.warn("calling taskService.asyncCall() on thread: "+Thread.currentThread().getName());
        try {
            Future<ResponseEntity<HashMap>> result =
                    asyncRestTemplate.getForEntity(profileUrl, HashMap.class);
            return Observable.from(result);

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Observable<ResponseEntity<HashMap>> syncCall() {
        LOGGER.warn("calling taskService.syncCall() on thread: "+Thread.currentThread().getName());
        try {
            Future<ResponseEntity<HashMap>> result =
                    localAsyncRestTemplate.getForEntity(profileUrl, HashMap.class);
            return Observable.from(result);

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}