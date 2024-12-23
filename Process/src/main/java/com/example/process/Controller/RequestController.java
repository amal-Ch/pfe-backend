package com.example.process.Controller;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.StatusDto;
import com.example.process.DTO.TaskDTO;
import com.example.process.exception.ResourceNotFoundException;
import com.example.process.repository.ProcessRepository;
import com.example.process.service.requestService.CamundaService;
import com.example.process.service.requestService.IServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
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

            String decision = variables.get("decision").toString();
            String decisionMessage = mapDecisionToMessage(decision);


            camundaService.completeTask(taskId, variables);
            //camundaService.notifyCamundaOfTaskCompletion(new StatusDto(2, processInstanceId, "User Task Completed"));
            requestService.sendNotificationEmail(decisionMessage);


            return ResponseEntity.ok(Collections.singletonMap("message", "Task completed successfully for task ID: " + taskId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error completing task: " + e.getMessage()));
        }
    }
    @GetMapping("/request-by-workflowTitle")
    public ResponseEntity<List<Object[]>> getRequestStatistics() {
        List<Object[]> statistics = requestService.getRequestCountByWorkflowTitle();
        return ResponseEntity.ok(statistics);
    }
    private String mapDecisionToMessage(String decision) {
        switch (decision) {
            case "3":
                return "Rejected by the Manager";
            case "6":
                return "Rejected by the HR";
            case "7":
                return "Accepted";
            default:
                return "Processed with unknown decision";
        }
    }
    @GetMapping("/count-by-date")
    public ResponseEntity<List<Object[]>> getRequestCountsGroupedByDate() {
        List<Object[]> counts = requestService.getRequestCountsGroupedByDate();
        return ResponseEntity.ok(counts);
    }
    @GetMapping("/Stat-requests-by-status")
    public List<Object[]> getRequestCountsByStatusTitle() {
        return requestService.getRequestCountsByStatusTitle();
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

    @GetMapping("/requests-by-id")
    public ResponseEntity<Page<RequestDTO>> getAllPagedRequestByIdUser(
            @RequestParam(value = "userId", required = true) Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<RequestDTO> requestDTOS=requestService.getRequestByUser(pageable,userId);
    return new ResponseEntity<>(requestDTOS,HttpStatus.OK);
    }
    @GetMapping("/by-status")
    public ResponseEntity<Page<RequestDTO>> getRequestsByStatus(
            @RequestParam Integer statusId,
            @RequestParam String statusTitle,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestDTO> requests = requestService.getRequestsByStatus(pageable, statusId, statusTitle);
        return ResponseEntity.ok(requests);
    }
    @GetMapping("/by-user-and-status")
    public ResponseEntity<Page<RequestDTO>> getRequestsByUserAndStatus(
            @RequestParam Long userId,
            @RequestParam Integer statusId,
            @RequestParam String statusTitle,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestDTO> requests = requestService.getRequestsByUserAndStatus(userId, statusId, statusTitle, pageable);
        return ResponseEntity.ok(requests);
    }

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

    @PostMapping("/UpdateRequestByProcessId/{processInstanceId}/{statusId}")
    public RequestDTO updateStatusByProcessId(
            @PathVariable String processInstanceId,
            @PathVariable Integer statusId) {
        // Update request status and convert to DTO
        return RequestDTO.toDTO(requestService.updateRequestByProcessId(processInstanceId, statusId));
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
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RequestDTO> requestDtos = requestService.searchRequests(search, pageable);
        return ResponseEntity.ok(requestDtos);
    }

}
