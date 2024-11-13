package com.example.process.Controller;

import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.WorkflowProcess;
import com.example.process.service.requestService.CamundaService;
import com.example.process.service.workflowService.IServiceWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {

    @Autowired
    private IServiceWorkflow processService;

    @Autowired
    private CamundaService camundaService;

    @PostMapping("/{processKey}/start")
    public void startProcess(@PathVariable String processKey) {
        camundaService.startProcess(processKey);
    }
    @GetMapping("/AllProcesses")
    public List<WorkflowDto> getAllProcesses() {
        return processService.getAllProcesses();
    }

    @GetMapping("/GetOneProcessByID/{id}")
    public ResponseEntity<WorkflowProcess> getProcessById(@PathVariable Integer id) {
        WorkflowProcess process = processService.getProcessById(id)
                .orElseThrow(() -> new RuntimeException("Process not found"));
        return ResponseEntity.ok().body(process);
    }
    @PostMapping("/AddProcessWithDiagram")
    public ResponseEntity<?> addProcessWithDiagram(@RequestBody WorkflowProcess payload) {
        try {
            // Save workflow with processKey and diagram XML
            WorkflowProcess createdWorkflow = processService.createProcessWithDiag(payload);
            return ResponseEntity.ok(createdWorkflow);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving workflow and diagram: " + e.getMessage());
        }
    }

    @PostMapping("/AddProcess")
    public ResponseEntity<WorkflowProcess> createProcess(@RequestBody WorkflowProcess process) {
        WorkflowProcess createdProcess = processService.createProcess(process);
        return ResponseEntity.ok().body(createdProcess);
    }

    @PutMapping("UpdateProcess/{id}")
    public ResponseEntity<WorkflowProcess> updateProcess(@PathVariable Integer id, @RequestBody WorkflowProcess processDetails) {
        WorkflowProcess updatedProcess = processService.updateProcess(id, processDetails);
        return ResponseEntity.ok().body(updatedProcess);
    }

    @DeleteMapping("DeleteProcess/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable Integer id) {
        processService.deleteProcess(id);
        return ResponseEntity.noContent().build();
    }

@GetMapping
public ResponseEntity<Page<WorkflowDto>> getAllWorkflowPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<WorkflowDto> workflowDtos = processService.getAllPageWorkflow(pageable);
    return ResponseEntity.ok(workflowDtos);
}
    @GetMapping("/search")
    public ResponseEntity<Page<WorkflowDto>> searchWorkflows(
            @RequestParam (value = "search",required = false) String search,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkflowDto> workflowDtos = processService.searchWorkflows(search, pageable);
        return ResponseEntity.ok(workflowDtos);
    }

}
