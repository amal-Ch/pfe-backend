package com.example.process.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("delegateController")
public class DelegateController implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            String activityId = execution.getCurrentActivityId();
            if ("Activity_0kz4la3".equals(activityId)) {
                serviceTask1(execution);
            } else if ("Activity_1mdhaz6".equals(activityId)) {
                serviceTask2(execution);
            }
        } catch (Exception e) {
            // Log and rethrow the exception
            System.err.println("Error executing service task: " + e.getMessage());
            throw e;
        }
    }


    public void serviceTask1(DelegateExecution execution) {
        // Business logic for service task 1
        System.out.println("You are in Service task 1");
    }

    public void serviceTask2(DelegateExecution execution) {
        // Business logic for service task 2
        System.out.println("You are in Service task 2");
    }
}
