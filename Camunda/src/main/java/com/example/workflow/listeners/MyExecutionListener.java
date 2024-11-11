package com.example.workflow.listeners;

import com.example.workflow.Controller.listenerController;
import com.example.workflow.DTO.StatusDto;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("myExecutionListener")
public class MyExecutionListener implements ExecutionListener {
    @Autowired
    listenerController listenerController;
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String currentActivityId = delegateExecution.getCurrentActivityId();
        System.out.println("Current Step: " + currentActivityId);

        String processInstanceId = delegateExecution.getProcessInstanceId();
        Integer statusId = loadStatusMap(processInstanceId);

        // Create StatusDto with statusId and processInstanceId
        StatusDto statusDto = new StatusDto(statusId, processInstanceId, "new"); // Replace "Status Title" with the actual title if available

        listenerController.notify(statusDto);
    }

    private Integer loadStatusMap(String processInstanceId) {
        // Here you can dynamically determine the statusId based on logic
        return 1; // Example static ID
    }
}
