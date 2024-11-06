package com.example.workflow.Controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/listener")
public class listenerController {

    private final RestTemplate restTemplate;

    public listenerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/UpdateRequestByProcessId/{ProcessInstanceId}/{statusId}")
    public void notify(String ProcessInstanceId, Integer statusId){
        String url = String.format("http://localhost:8096/api/requests/UpdateRequestByProcessId/%s/%s", ProcessInstanceId, statusId);

        try {
            restTemplate.postForEntity(url, null, Void.class);  // Use Void.class if response body is not needed
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to update request: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            System.err.println("Resource access error: " + e.getMessage());
            throw new RuntimeException("Camunda server not reachable: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage(), e);
        }}}

