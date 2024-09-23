package com.example.workflow.Controller;

import com.example.workflow.DTO.TaskDto;
import com.example.workflow.services.ProcessDeployer;
import com.example.workflow.services.ProcessService;
import org.camunda.bpm.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

//    @PostMapping("process-definition/key/{processKey}/start-with-variables")
//public void startProcessWithVariables(@PathVariable String processKey, @RequestBody StartProcessInstanceDto dto) {
//    processService.startProcessWithVariables(processKey, dto);
//}

    @GetMapping("/engine-rest/task")
    public ResponseEntity<List<TaskDto>> getTasks(@RequestParam String processInstanceId) {
        List<TaskDto> tasks = processService.getTasks(processInstanceId);
        return ResponseEntity.ok(tasks);
    }

//    @GetMapping("/engine-rest/tasks/{processInstanceId}")
//    public ResponseEntity<List<TaskDto>> getTasks(@PathVariable String processInstanceId) {
//        List<TaskDto> tasks = processService.getTasks(processInstanceId);
//        return ResponseEntity.ok(tasks);
//    }

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


//    @GetMapping("/engine-rest/variables/{processInstanceId}")
//    public Map<String, Object> getProcessVariables(@PathVariable String processInstanceId) {
//        return processService.getProcessVariables(processInstanceId);
//    }
//    public Object getTaskVariable(@PathVariable String id, @PathVariable String varName) {
//        return taskService.getVariable(id, varName);
//    }

    // Endpoint to set process variables
//    @PostMapping("/engine-rest/variables/{processInstanceId}")
//    public void setProcessVariables(@PathVariable String processInstanceId, @RequestBody Map<String, Object> variables) {
//        processService.setProcessVariables(processInstanceId, variables);
//    }

}


