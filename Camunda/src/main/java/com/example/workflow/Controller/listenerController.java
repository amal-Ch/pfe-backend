package com.example.workflow.Controller;


import com.example.workflow.DTO.StatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/listener")
public class listenerController {

    private final RestTemplate restTemplate;

    public listenerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> notify(@RequestBody StatusDto statusDto) {
        System.out.println("Received notify request for ProcessInstanceID: " + statusDto.getProcessInstanceId());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/notifyTaskCompletion")
    public ResponseEntity<Void> notifyTaskCompletion(@RequestBody StatusDto statusDto) {
        System.out.println("Received task completion notification for ProcessInstanceID: " + statusDto.getProcessInstanceId());
        return ResponseEntity.ok().build();
    }
}


