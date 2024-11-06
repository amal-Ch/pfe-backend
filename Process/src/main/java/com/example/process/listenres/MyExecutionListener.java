package com.example.process.listenres;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class MyExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String currentActivityId = delegateExecution.getCurrentActivityId();
        System.out.println("Current Step: " + currentActivityId);
    }
}
