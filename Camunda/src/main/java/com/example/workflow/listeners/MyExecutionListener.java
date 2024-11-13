package com.example.workflow.listeners;

import com.example.workflow.Controller.listenerController;
import com.example.workflow.DTO.StatusDto;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component("myExecutionListener")
public class MyExecutionListener implements ExecutionListener {
    @Autowired
    listenerController listenerController;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public void notify(DelegateExecution delegateExecution) {
        // Retrieve the "decision" variable as a String and convert it to Integer
        Object decisionValue = delegateExecution.getVariable("decision");

        Integer decision;
        if (decisionValue instanceof String) {
            try {
                decision = Integer.parseInt((String) decisionValue);
            } catch (NumberFormatException e) {
                System.err.println("Error: Decision variable is not a valid integer.");
                return; // Exit the method if parsing fails
            }
        } else if (decisionValue instanceof Integer) {
            decision = (Integer) decisionValue;
        } else {
            System.err.println("Error: Decision variable is of an unexpected type.");
            return;
        }

        // Proceed with using the decision variable as an Integer
        String currentActivityId = delegateExecution.getCurrentActivityId();
        System.out.println("Current Step: " + currentActivityId);

        String processInstanceId = delegateExecution.getProcessInstanceId();
        Integer statusId = loadStatusMap(decision);

        // Call the process microservice to update the request status
        String processServiceUrl = "http://localhost:8096/api/requests/UpdateRequestByProcessId/"
                + processInstanceId + "/" + statusId;

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    processServiceUrl,
                    HttpMethod.POST,
                    null,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Request status updated successfully in process microservice");
            } else {
                System.err.println("Failed to update request status in process microservice: "
                        + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error calling process microservice: " + e.getMessage());
        }
    }

    private Integer loadStatusMap(Integer decision) {
        return decision; // Directly returning the decision as status ID
    }
}
