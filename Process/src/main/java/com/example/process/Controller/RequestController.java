package com.example.process.Controller;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.TaskDTO;
import com.example.process.entity.Request;
import com.example.process.exception.ResourceNotFoundException;
import com.example.process.repository.ProcessRepository;
import com.example.process.service.requestService.CamundaService;
import com.example.process.service.requestService.IServiceRequest;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private IServiceRequest requestService;

    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private CamundaService camundaService;

    @PostMapping("/complete-task/{processInstanceId}")
    public ResponseEntity<Map<String, String>> completeTaskByProcessInstanceId(
            @PathVariable String processInstanceId,
            @RequestParam Map<String, Object> variables) {
        try {
            List<TaskDTO> tasks = camundaService.getTasks(processInstanceId);

            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No tasks found for process instance ID: " + processInstanceId));
            }

            String taskId = tasks.get(0).getId(); // Get the first task

            // Ensure the "decision" variable is present in the request body
            if (!variables.isEmpty() && !variables.containsKey("decision")) {
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

//    @PostMapping("/AddRequestWithStatus")
//    public ResponseEntity<Request> createRequest(@RequestBody Map<String, Object> requestData) {
//        String fullName = (String) requestData.get("fullName");
//        String object = (String) requestData.get("object");
//        Integer statusId = (Integer) requestData.get("statusId");
//
//        Request newRequest = new Request();
//        newRequest.setFullName(fullName);
//        newRequest.setObject(object);
//
//        Request createdRequest = requestService.createRequestWithStatus(newRequest, statusId);
//        return ResponseEntity.ok(createdRequest);
//    }

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
    @PostMapping("UpdateRequestByProcessId/{ProcessInstanceId}/{statusId}")
    public void updateStatusByProcessId(@PathVariable String ProcessInstanceId, @PathVariable Integer statusId) {
        requestService.updateStatusByProcessId(ProcessInstanceId,statusId);
    }

    @DeleteMapping("DeleteRequest/{id}")
    public void deleteRequest(@PathVariable Integer id) {
        requestService.deleteRequest(id);
    }

    @GetMapping
    public ResponseEntity<Page<RequestDTO>> getAllPageRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestDTO> requestDtos = requestService.getAllPageRequest(pageable);
        return ResponseEntity.ok(requestDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RequestDTO>> searchRequests(
            @RequestParam(value = "search",required = false) String search,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestDTO> requestDtos = requestService.searchRequests(search, pageable);
        return ResponseEntity.ok(requestDtos);
    }

}
