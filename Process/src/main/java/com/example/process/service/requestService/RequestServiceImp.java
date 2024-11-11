package com.example.process.service.requestService;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.StatusDto;
import com.example.process.email.Service.impl.EmailServiceImpl;
import com.example.process.email.Service.impl.PdfGeneratorService;
import com.example.process.entity.Request;
import com.example.process.entity.Status;
import com.example.process.entity.WorkflowProcess;
import com.example.process.exception.ResourceNotFoundException;
import com.example.process.repository.ProcessRepository;
import com.example.process.repository.RequestRepository;
import com.example.process.repository.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestServiceImp implements IServiceRequest {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @Autowired
    private EmailServiceImpl emailService;

    private static final Logger logger = LoggerFactory.getLogger(RequestServiceImp.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;


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
        // Set userId from DTO
        request.setUserId(requestDTO.getUserId());

        Status newStatus = statusRepository.findByTitle("new")
                .orElseThrow(() -> new ResourceNotFoundException("Status 'New' not found"));
        request.setStatus(newStatus);

        // Start the process instance using the process key and get the process instance ID
        String processKey = workflowProcessOpt.get().getProcessKey();
        logger.info("Starting process with key: {}", processKey);
        String processInstanceId = camundaService.startProcess(processKey);

        // Set the process instance ID in the request entity
        request.setProcessInstanceId(processInstanceId);



        // Save the request entity to the database
        Request savedRequest = requestRepository.save(request);
        logger.info("Request saved: {}", savedRequest);
        // Generate PDF of the request data
        ByteArrayInputStream pdf = pdfGeneratorService.generatePdfForRequest(savedRequest);

        // Send email with the PDF as an attachment
        try {
            emailService.sendEmailWithAttachment("amalchibani3@outlook.com", "Your PDF", "Please find the attached PDF.", pdf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Convert the saved entity back to DTO and return it
        return convertToDto(savedRequest);
    }

//    public Request createRequestWithStatus(Request request, Integer statusId) {
//        Status status = statusRepository.findById(statusId)
//                .orElseThrow(() -> new RuntimeException("Status not found with id: " + statusId));
//
//        request.getStatuses().add(status);  // Add the "New" status to the Request
//        return requestRepository.save(request);
//    }

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

    @Override
    public Request updateRequestByProcessId(String processInstanceId, Integer statusId) {
        // Fetch and update the request by processInstanceId
        Request request = requestRepository.findByProcessInstanceId(processInstanceId)
                .orElseThrow(() -> new RuntimeException("Request not found with processInstanceId: " + processInstanceId));

        Status newStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + statusId));

        // Update the status in the request
        request.updateStatus(newStatus);
        Request updatedRequest = requestRepository.save(request);

        // Create a StatusDto to notify Camunda about the update
        StatusDto statusDto = new StatusDto(statusId, processInstanceId, newStatus.getTitle());

        camundaService.notifyCamundaService(statusDto);
        // Notify Camunda service
       // camundaService.notifyCamundaService(statusDto);

        return updatedRequest;
    }


    private RequestDTO convertToDto(Request request) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setIdRequest(request.getIdRequest());
        requestDTO.setFullName(request.getFullName());
        requestDTO.setObject(request.getObject());
        request.setUserId(requestDTO.getUserId());
        requestDTO.setAddedDateRequest(request.getAddedDateRequest());
        requestDTO.setIdProcess(request.getWorkflowProcess().getIdProcess());
        requestDTO.setProcessInstanceId(request.getProcessInstanceId());
        if (request.getStatus() != null) {
            requestDTO.setStatusId(request.getStatus().getIdStatus());
        } else {
            requestDTO.setStatusId(null); // or handle as appropriate
        }

        return requestDTO;
    }

    private Request convertToEntity(RequestDTO requestDTO) {
        Request request = new Request();
        request.setFullName(requestDTO.getFullName());
        request.setObject(requestDTO.getObject());
        request.setUserId(requestDTO.getUserId());
        request.setAddedDateRequest(requestDTO.getAddedDateRequest());
        request.setProcessInstanceId(requestDTO.getProcessInstanceId());

        return request;
    }

    public Page<RequestDTO> getAllPageRequest(Pageable pageable) {
        Page<Request> RequestPage = requestRepository.findAll(pageable);
        return RequestPage.map(RequestDTO::toDTO);
    }

    public Page<RequestDTO> searchRequests(String search, Pageable pageable) {
        Page<Request> requestPage = requestRepository.searchByFullName(search, pageable);
        return requestPage.map(RequestDTO::toDTO);
    }


    public void deleteRequest(Integer id) {
        requestRepository.deleteById(id);
    }
}
