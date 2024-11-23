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
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImp implements IServiceWorkflow{
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private BpmnFileService bpmnFileService;

    public List<Map<String, Object>> getWorkflowCountByYearAndMonth() {
        List<Object[]> rawData = processRepository.findWorkflowCountGroupedByYearAndMonth();
        List<Map<String, Object>> enhancedResponse = new ArrayList<>();

        for (Object[] row : rawData) {
            int year = ((Number) row[0]).intValue();  // Extract year
            int month = ((Number) row[1]).intValue(); // Extract month
            long workflowCount = ((Number) row[2]).longValue(); // Extract count

            // Create a formatted date string like "MM-YYYY"
            String formattedDate = String.format("%02d-%d", month, year);

            // Get the month's name
            String monthName = Month.of(month).name(); // Java's Month enum (in uppercase)
            monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase(); // Capitalize the first letter

            // Add data to the response map
            Map<String, Object> responseEntry = new HashMap<>();
            responseEntry.put("formattedDate", formattedDate);
            responseEntry.put("monthNumber", month);
            responseEntry.put("monthName", monthName);
            responseEntry.put("year", year);
            responseEntry.put("workflowCount", workflowCount);

            enhancedResponse.add(responseEntry);
        }

        return enhancedResponse;
    }
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
        process.setDiagramXML(processDetails.getDiagramXML());
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
