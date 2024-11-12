package com.example.workflow.listeners;

import com.example.workflow.DTO.StatusDto;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("taskCompletionTaskListener")
public class TaskCompletionTaskListener implements TaskListener {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void notify(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        Integer statusId = 2; // Example of custom logic for status ID

        StatusDto statusDto = new StatusDto(statusId, processInstanceId, "User Task Completed");

        // Notify the process microservice
        String url = "http://localhost:8094/api/listener/notifyTaskCompletion"; // process microservice URL
        restTemplate.postForEntity(url, statusDto, Void.class);

        System.out.println("User task completion notification sent for ProcessInstanceID: " + processInstanceId);
    }
}
