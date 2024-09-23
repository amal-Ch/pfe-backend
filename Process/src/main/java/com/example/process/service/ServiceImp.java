package com.example.process.service;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.Request;
import com.example.process.entity.WorkflowProcess;
import com.example.process.exception.ResourceNotFoundException;
import com.example.process.repository.ProcessRepository;
import com.example.process.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceImp implements IServices {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private CamundaService camundaService;
    @Autowired
    private BpmnFileService bpmnFileService;

    private static final Logger logger = LoggerFactory.getLogger(ServiceImp.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    ///////////////////PROCESSES///////////////////////
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
    ///////////////////REQUESTES///////////////////////


    public List<RequestDTO> getAllRequests() {

        List<Request> requests = requestRepository.findAll();
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDTO addRequest(RequestDTO requestDTO) throws ResourceNotFoundException {
        // Retrieve the associated workflow process
        Optional<WorkflowProcess> workflowProcessOpt = processRepository.findById(requestDTO.getIdProcess());
        if (!workflowProcessOpt.isPresent()) {
            throw new ResourceNotFoundException("Workflow Process not found with id " + requestDTO.getIdProcess());
        }

        // Convert DTO to entity and set the workflow process
        Request request = convertToEntity(requestDTO);
        request.setWorkflowProcess(workflowProcessOpt.get());

        // Start the process instance using the process key and get the process instance ID
        String processKey = workflowProcessOpt.get().getProcessKey();
        logger.info("Starting process with key: {}", processKey);
        String processInstanceId = camundaService.startProcess(processKey);

        // Set the process instance ID in the request entity
        request.setProcessInstanceId(processInstanceId);

        // Save the request entity to the database
        Request savedRequest = requestRepository.save(request);
        logger.info("Request saved: {}", savedRequest);

        // Convert the saved entity back to DTO and return it
        return convertToDto(savedRequest);
    }

    @Override
    public Request saveRequest(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public RequestDTO updateRequest(Integer id, RequestDTO requestDTO) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setFullName(requestDTO.getFullName());
        request.setObject(requestDTO.getObject());
        request.setAddedDateRequest(requestDTO.getAddedDateRequest());

        WorkflowProcess workflowProcess = processRepository.findById(requestDTO.getIdProcess())
                .orElseThrow(() -> new ResourceNotFoundException("Process not found"));

        request.setWorkflowProcess(workflowProcess);

        Request updatedRequest = requestRepository.save(request);
        return convertToDto(updatedRequest);
    }

    private RequestDTO convertToDto(Request request) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setIdRequest(request.getIdRequest());
        requestDTO.setFullName(request.getFullName());
        requestDTO.setObject(request.getObject());
        requestDTO.setAddedDateRequest(request.getAddedDateRequest());
        requestDTO.setIdProcess(request.getWorkflowProcess().getIdProcess());
        requestDTO.setProcessInstanceId(request.getProcessInstanceId()); // Add this line
        return requestDTO;
    }

    private Request convertToEntity(RequestDTO requestDTO) {
        Request request = new Request();
        request.setFullName(requestDTO.getFullName());
        request.setObject(requestDTO.getObject());
        request.setAddedDateRequest(requestDTO.getAddedDateRequest());
        request.setProcessInstanceId(requestDTO.getProcessInstanceId()); // Add this line
        return request;
    }

    public Page<WorkflowDto> getAllPage(Pageable pageable) {
        Page<WorkflowProcess> workflowProcessPage = processRepository.findAll(pageable);
        return workflowProcessPage.map(WorkflowDto::toDTO);

    }

    public void deleteRequest(Integer id) {
        requestRepository.deleteById(id);
    }
}
