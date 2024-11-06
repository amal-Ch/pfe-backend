package com.example.process.service.statusService;

import com.example.process.entity.Request;
import com.example.process.entity.Status;
import com.example.process.entity.WorkflowProcess;
import com.example.process.repository.ProcessRepository;
import com.example.process.repository.RequestRepository;
import com.example.process.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatusServiceImp implements IStatusService{

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Override
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    @Override
    public Status createStatus(Map<String, Object> requestData) {
        Status status = new Status();
        status.setTitle((String) requestData.get("title"));

        // Map workflow process IDs to WorkflowProcess entities
        List<Integer> workflowProcessIds = (List<Integer>) requestData.get("workflowProcessIds");
        Set<WorkflowProcess> workflowProcesses = workflowProcessIds.stream()
                .map(id -> processRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("WorkflowProcess not found with id: " + id)))
                .collect(Collectors.toSet());
        status.setWorkflowProcesses(workflowProcesses);

        return statusRepository.save(status);
    }

    @Override
    public Status updateStatus(Integer idStatus, Status statusDetails) {
        return statusRepository.findById(idStatus)
                .map(status -> {
                    status.setTitle(statusDetails.getTitle());

                    // Clear and update associated workflow processes
                    status.getWorkflowProcesses().clear();
                    status.getWorkflowProcesses().addAll(statusDetails.getWorkflowProcesses());

                    return statusRepository.save(status);
                })
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + idStatus));
    }

    @Override
    public void deleteStatus(Integer idStatus) {
        statusRepository.deleteById(idStatus);
    }
}
