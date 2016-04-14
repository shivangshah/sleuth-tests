package com.shivang.performance;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TaskServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    
    public Map<String, Boolean> execute() {
        try {
            Thread.sleep(100);
            LOGGER.warn("Slow task executed");
            HashMap<String, Boolean> result = new HashMap<>();
            result.put("success", true);
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}