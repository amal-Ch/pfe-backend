package com.example.process.Controller;

import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.WorkflowProcess;
import com.example.process.service.CamundaService;
import com.example.process.service.IServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {

    @Autowired
    private IServices processService;

    @Autowired
    private CamundaService camundaService;

    @PostMapping("/{processKey}/start")
    public void startProcess(@PathVariable String processKey) {
        camundaService.startProcess(processKey);
    }
    @GetMapping("/AllProcesses")
    public List<WorkflowProcess> getAllProcesses() {
        return processService.getAllProcesses();
    }

    @GetMapping("/GetOneProcessByID/{id}")
    public ResponseEntity<WorkflowProcess> getProcessById(@PathVariable Integer id) {
        WorkflowProcess process = processService.getProcessById(id)
                .orElseThrow(() -> new RuntimeException("Process not found"));
        return ResponseEntity.ok().body(process);
    }
//    public ResponseEntity<WorkflowProcess> getProcessById(@PathVariable Integer id) {
//        Optional<WorkflowProcess> process = processService.getProcessById(id);
//        if (process.isPresent()) {
//            return ResponseEntity.ok(process.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping("/AddProcess")
    public ResponseEntity<WorkflowProcess> createProcess(@RequestBody WorkflowProcess process) {
        WorkflowProcess createdProcess = processService.createProcess(process);
        return ResponseEntity.ok().body(createdProcess);
    }
//    public WorkflowProcess createProcess(@RequestBody WorkflowProcess process) {
//        return processService.createProcess(process);
//    }

    @PutMapping("UpdateProcess/{id}")
    public ResponseEntity<WorkflowProcess> updateProcess(@PathVariable Integer id, @RequestBody WorkflowProcess processDetails) {
        WorkflowProcess updatedProcess = processService.updateProcess(id, processDetails);
        return ResponseEntity.ok().body(updatedProcess);
    }
//    public ResponseEntity<WorkflowProcess> updateProcess(@PathVariable Integer id, @RequestBody WorkflowProcess processDetails) {
//        try {
//            WorkflowProcess updatedProcess = processService.updateProcess(id, processDetails);
//            return ResponseEntity.ok(updatedProcess);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("DeleteProcess/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable Integer id) {
        processService.deleteProcess(id);
        return ResponseEntity.noContent().build();
    }
//    public ResponseEntity<Void> deleteProcess(@PathVariable Integer id) {
//        try {
//            processService.deleteProcess(id);
//            return ResponseEntity.noContent().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
@GetMapping
public ResponseEntity<Page<WorkflowDto>> getAllWorkflowPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<WorkflowDto> workflowDtos = processService.getAllPage(pageable);
    return ResponseEntity.ok(workflowDtos);
}
}
