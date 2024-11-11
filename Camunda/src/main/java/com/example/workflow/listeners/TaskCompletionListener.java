package com.example.workflow.listeners;

import com.example.workflow.Controller.listenerController;
import com.example.workflow.DTO.StatusDto;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("taskCompletionListener")
public class TaskCompletionListener implements ExecutionListener {
    @Autowired
    listenerController listenerController;


@Autowired
private RestTemplate restTemplate;

    @Override
    public void notify(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        Integer statusId = 2;  // Custom status ID for completed tasks

        StatusDto statusDto = new StatusDto(statusId, processInstanceId, "Task Completed");

        // Send notification to process microservice
        String url = "http://localhost:8094/api/listener/notifyTaskCompletion"; // process microservice URL
        restTemplate.postForEntity(url, statusDto, Void.class);

        System.out.println("Task completion notification sent for ProcessInstanceID: " + processInstanceId);
    }

}
