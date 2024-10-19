package com.example.process.service.workflowService;

import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.WorkflowProcess;
import com.example.process.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowServiceImp implements IServiceWorkflow{
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private BpmnFileService bpmnFileService;

    @Override
    public List<WorkflowProcess> getAllProcesses() {
        return processRepository.findAll();
    }

    @Override
    public Optional<WorkflowProcess> getProcessById(Integer id) {
        return processRepository.findById(id);
    }

    @Override
    public WorkflowProcess createProcess(WorkflowProcess process) {
        String bpmnContent = bpmnFileService.loadBpmnFile(process.getProcessKey());
        if (bpmnContent == null) {
            throw new RuntimeException("BPMN file not found");
        }
        return processRepository.save(process);
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
