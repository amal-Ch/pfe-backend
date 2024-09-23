package com.example.process.Controller;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.TaskDTO;
import com.example.process.entity.Request;
import com.example.process.entity.WorkflowProcess;
import com.example.process.exception.ResourceNotFoundException;
import com.example.process.repository.ProcessRepository;
import com.example.process.repository.RequestRepository;
import com.example.process.service.CamundaService;
import com.example.process.service.IServices;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:4200")
public class RequestController {

    @Autowired
    private IServices requestService;

    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private CamundaService camundaService;
//    @GetMapping("/engine-rest/tasks/{processInstanceId}")
//    public ResponseEntity<List<TaskDto>> getTasks(@PathVariable String processInstanceId) {
//        List<TaskDto> tasks = camundaService.getTasks(processInstanceId);
//        return ResponseEntity.ok(tasks);
//    }
@PostMapping("/complete-task/{processInstanceId}")
public ResponseEntity<Map<String, String>> completeTaskByProcessInstanceId(
        @PathVariable String processInstanceId,
        @RequestBody Map<String, Object> variables) {
    try {
        List<TaskDTO> tasks = camundaService.getTasks(processInstanceId);

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No tasks found for process instance ID: " + processInstanceId));
        }

        String taskId = tasks.get(0).getId(); // Get the first task

        // Ensure the "decision" variable is present in the request body
        if (!variables.containsKey("decision")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Missing 'decision' variable in request body"));
        }

        camundaService.completeTask(taskId, variables);

        return ResponseEntity.ok(Collections.singletonMap("message", "Task completed successfully for task ID: " + taskId));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Error completing task: " + e.getMessage()));
    }
}

    @GetMapping("/AllRequests")
    public List<RequestDTO> getAllRequests() {
        return requestService.getAllRequests();
    }
    @PostMapping("/AddRequest")
    public ResponseEntity<RequestDTO> addRequest(@RequestBody RequestDTO requestDTO) {
        try {
            // Save the request and start the process in the service layer
            RequestDTO createdRequest = requestService.addRequest(requestDTO);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PostMapping("/AddRequest")
//    public ResponseEntity<RequestDTO> addRequest(@RequestBody RequestDTO requestDTO) {
//        try {
//            RequestDTO createdRequest = requestService.addRequest(requestDTO);
//            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
//        } catch (ResourceNotFoundException e) {
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//}
    @PutMapping("UpdateRequest/{id}")
    public ResponseEntity<RequestDTO> updateRequest(@PathVariable Integer id, @RequestBody RequestDTO requestDTO) {
        try {
            RequestDTO updatedRequest = requestService.updateRequest(id, requestDTO);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("DeleteRequest/{id}")
    public void deleteRequest(@PathVariable Integer id) {
        requestService.deleteRequest(id);
    }
}
