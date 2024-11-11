package com.example.process.service.requestService;

import com.example.process.DTO.StatusDto;
import com.example.process.DTO.TaskDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CamundaService {
    private final RestTemplate restTemplate;

    public CamundaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyCamundaOfTaskCompletion(StatusDto statusDto) {
        String url = "http://localhost:8094/api/listener/notifyTaskCompletion"; // URL in Camunda microservice

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StatusDto> requestEntity = new HttpEntity<>(statusDto, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(url, requestEntity, Void.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Camunda microservice notified of task completion");
            } else {
                System.err.println("Failed to notify Camunda, status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error notifying Camunda: " + e.getMessage(), e);
        }
    }
    public void notifyCamundaService(StatusDto statusDto) {
        String url = "http://localhost:8094/api/listener/notify"; // URL to Camunda microservice listener endpoint

        try {
            // Set headers and make a POST request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StatusDto> requestEntity = new HttpEntity<>(statusDto, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(url, requestEntity, Void.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Successfully notified Camunda service");
            } else {
                System.err.println("Failed to notify Camunda service, status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error notifying Camunda service: " + e.getMessage(), e);
        }
    }
    public String startProcess(String processKey) {
        String url = "http://localhost:8094/api/engine-rest/process-definition/key/" + processKey + "/start";
        try {
            // Prepare the headers and entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            // Make the POST request and capture the response
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse the response body to extract the processInstanceId
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            // Retrieve the processInstanceId from the JSON response
            if (jsonResponse.has("id")) {
                return jsonResponse.get("id").asText();
            } else {
                throw new RuntimeException("Response does not contain an 'id' field");
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            throw new RuntimeException("Failed to start process: " + e.getMessage(), e);
        }
    }
    public List<TaskDTO> getTasks(String processInstanceId) {
        String url = "http://localhost:8094/api/engine-rest/task?processInstanceId=" + processInstanceId;
        try {
            ResponseEntity<TaskDTO[]> response = restTemplate.getForEntity(url, TaskDTO[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get tasks: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            System.err.println("Resource access error: " + e.getMessage());
            throw new RuntimeException("Camunda server not reachable: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage(), e);
        }
    }


    public void completeTask(String taskId, Map<String, Object> variables) {
        String url = "http://localhost:8094/api/engine-rest/task/" + taskId + "/complete";
        try {
            // If variables are provided, include them in the request body
            if (variables != null && !variables.isEmpty()) {
                restTemplate.postForObject(url, variables, String.class);
            } else {
                // Send empty request body if no variables
                restTemplate.postForObject(url, Collections.emptyMap(), String.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }
    public Map<String, Object> getTaskRequiredVariables(String taskId) {
        String url = "http://localhost:8094/api/engine-rest/task/" + taskId + "/form-variables";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();  // This will give you the required variables
        } catch (Exception e) {
            throw new RuntimeException("Failed to get task required variables: " + e.getMessage(), e);
        }
    }

}
