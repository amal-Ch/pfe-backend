package com.example.process.service.workflowService;

import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.WorkflowProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IServiceWorkflow {
        List<Map<String, Object>> getWorkflowCountByYearAndMonth();
        List<WorkflowDto> getAllProcesses();
        Optional<WorkflowProcess> getProcessById(Integer id);
        WorkflowProcess createProcessWithDiag(WorkflowProcess process);
        WorkflowProcess createProcess(WorkflowProcess process);

        WorkflowProcess updateProcess(Integer id, WorkflowProcess processDetails);
        void deleteProcess(Integer id);
        Page<WorkflowDto> getAllPageWorkflow(Pageable pageable);
        Page<WorkflowDto> searchWorkflows(String query,Pageable pageable);


}
