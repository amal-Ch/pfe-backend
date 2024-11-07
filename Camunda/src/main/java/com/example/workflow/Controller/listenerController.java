package com.example.workflow.Controller;


import com.example.workflow.DTO.StatusDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("UpdateRequestByProcessId")
    public StatusDto notify(@RequestBody StatusDto statusDto) {
        String url = "http://localhost:8096/api/requests/UpdateRequestByProcessId/"
                + statusDto.getProcessInstanceId() + "/" + statusDto.getStatusId();

        ResponseEntity<StatusDto> response = restTemplate.postForEntity(url, null, StatusDto.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to update request: " + response.getStatusCode());
        }
    }
}


