package com.example.process.service;

import com.example.process.DTO.TaskDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CamundaService {
    private final RestTemplate restTemplate;

    public CamundaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
                restTemplate.postForObject(url, null, String.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete task: " + e.getMessage(), e);
        }
    }

}
