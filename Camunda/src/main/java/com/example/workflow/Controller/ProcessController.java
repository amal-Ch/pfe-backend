package com.example.workflow.Controller;

import com.example.workflow.DTO.TaskDto;
import com.example.workflow.services.ProcessDeployer;
import com.example.workflow.services.ProcessService;
import org.camunda.bpm.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class ProcessController {
    @Autowired
    private final ProcessService processService;
    @Autowired
    private ProcessDeployer processDeployer;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/amal")
    public void amal(){
        System.out.println("hello amal");
    }

    @PostMapping("/engine-rest/process-definition/key/{processKey}/start-and-complete")
    public ResponseEntity<String> startAndCompleteProcess(@PathVariable String processKey) {
        processService.startAndCompleteProcess(processKey);
        return ResponseEntity.ok("Process started and tasks completed");
    }

    @PostMapping("/deployment/create")
    public void deployProcess(@RequestParam("file") MultipartFile file, @RequestParam("deployment-name") String deploymentName) {
        try {
            processDeployer.deployProcess(file.getInputStream(), deploymentName);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
    //StartSimple that returnes processInstanceId
    @PostMapping("/engine-rest/process-definition/key/{processKey}/start")
    public ResponseEntity<Map<String, String>> startProcess(@PathVariable String processKey) {
        String processInstanceId = processService.startProcess(processKey);
        Map<String, String> response = new HashMap<>();
        response.put("id", processInstanceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/engine-rest/task")
    public ResponseEntity<List<TaskDto>> getTasks(@RequestParam String processInstanceId) {
        List<TaskDto> tasks = processService.getTasks(processInstanceId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/engine-rest/task/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> inputVariables) {
        try {
            // Log incoming variables for debugging
            logger.info("Completing task with ID {} with variables: {}", taskId, inputVariables);

            // Call the service to complete the task
            processService.completeTask(taskId, inputVariables);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to complete task with ID {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // New endpoint to get form variables for a task
    @GetMapping("/engine-rest/task/{taskId}/form-variables")
    public ResponseEntity<Map<String, Object>> getTaskFormVariables(@PathVariable String taskId) {
        try {
            Map<String, Object> formVariables = processService.getTaskFormVariables(taskId);
            return ResponseEntity.ok(formVariables);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to retrieve form variables: " + e.getMessage()));
        }
    }
}


