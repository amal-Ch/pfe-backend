package com.example.process.service.workflowService;

import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.WorkflowProcess;
import com.example.process.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImp implements IServiceWorkflow{
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private BpmnFileService bpmnFileService;

    @Override
    public List<WorkflowDto> getAllProcesses() {
        return processRepository.findAll().stream()
                .map(WorkflowDto::toDTO)  // Using the static toDTO method
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkflowProcess> getProcessById(Integer id) {
        return processRepository.findById(id);
    }

    @Override
    public WorkflowProcess createProcessWithDiag(WorkflowProcess process) {
        // Step 1: Save the workflow to the database
        WorkflowProcess savedProcess = processRepository.save(process);

        // Step 2: Save the diagram XML to resources/WorkFlows using processKey
        if (process.getDiagramXML() != null && !process.getDiagramXML().isEmpty()) {
            bpmnFileService.saveBpmnFile(process.getProcessKey(), process.getDiagramXML());
        }

        // Step 3: Optional - Verify by loading the saved file
        try {
            String bpmnContent = bpmnFileService.loadBpmnFile(process.getProcessKey());
            if (bpmnContent == null) {
                throw new RuntimeException("Failed to load the BPMN file after saving.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading saved BPMN file: " + e.getMessage(), e);
        }

        return savedProcess;
//        String bpmnContent = bpmnFileService.loadBpmnFile(process.getProcessKey());
//        if (bpmnContent == null) {
//            throw new RuntimeException("BPMN file not found");
//        }
       // return processRepository.save(process);
    }
    @Override
    public WorkflowProcess createProcess(WorkflowProcess process) {
        // Step 1: Save the workflow to the database
        WorkflowProcess savedProcess = processRepository.save(process);

        // Step 3: Optional - Verify by loading the saved file
        try {
            String bpmnContent = bpmnFileService.loadBpmnFile(process.getProcessKey());
            if (bpmnContent == null) {
                throw new RuntimeException("Failed to load the BPMN file after saving.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading saved BPMN file: " + e.getMessage(), e);
        }

        return savedProcess;
    }


    @Override
    public WorkflowProcess updateProcess(Integer id, WorkflowProcess processDetails) {
        WorkflowProcess process = processRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        process.setCode(processDetails.getCode());
        process.setTitle(processDetails.getTitle());
        process.setProcessKey(processDetails.getProcessKey());
        String bpmnContent = bpmnFileService.loadBpmnFile(process.getProcessKey());
        if (bpmnContent == null) {
            throw new RuntimeException("BPMN file not found");
        }

        return processRepository.save(process);
    }

    @Override
    public void deleteProcess(Integer id) {
        WorkflowProcess process = processRepository.findById(id).orElseThrow(() -> new RuntimeException("Process not found"));
        processRepository.delete(process);
    }

    public Page<WorkflowDto> getAllPageWorkflow(Pageable pageable) {
        Page<WorkflowProcess> workflowProcessPage = processRepository.findAll(pageable);
        return workflowProcessPage.map(WorkflowDto::toDTO);
    }

    public Page<WorkflowDto> searchWorkflows(String search, Pageable pageable) {
        Page<WorkflowProcess> workflowProcessPage = processRepository.searchByTitle(search, pageable);
        return workflowProcessPage.map(WorkflowDto::toDTO);
}
}
