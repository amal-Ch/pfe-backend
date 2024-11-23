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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public List<Object[]> getRequestCountsGroupedByDate() {
        return requestRepository.findRequestCountGroupedByDate();
    }

    @Override
    public List<Object[]> getRequestCountsByStatusTitle() {
        return requestRepository.countRequestsByStatusTitle();
    }

    public void sendNotificationEmail(String decisionMessage) {
        try {
            String to = "amalchibani3@outlook.com"; // Replace with recipient's email
            String subject = " Request Response ";
            String body = String.format("Your request has been processed with the decision: %s.", decisionMessage);

            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);

            emailService.sendMail(null, to, null, subject, body, null);
            System.out.println("Notification email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Error sending notification email: " + e.getMessage());
        }
    }

    private void sendEmailWithPdf(ByteArrayInputStream pdf) {
        try {
            emailService.sendEmailWithAttachment(
                    "amalchibani3@outlook.com",
                    "Your PDF",
                    "Please find the attached PDF.",
                    pdf
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to send email with PDF attachment", e);
        }
    }

    public List<RequestDTO> getAllRequests() {

        List<Request> requests = requestRepository.findAll();
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RequestDTO> getRequestByUser(Pageable pageable,Long userId) {
        Page<Request> requests= requestRepository.findByUserId(pageable,userId);
        return requests.map(RequestDTO::toDTO);
    }

    @Override
    public Page<RequestDTO> getRequestsByStatus(Pageable pageable, Integer statusId, String statusTitle) {
        Page<Request> requests = requestRepository.findByStatus_IdStatusAndStatus_Title(pageable, statusId, statusTitle);
        return requests.map(RequestDTO::toDTO);
    }
    @Override
    public Page<RequestDTO> getRequestsByUserAndStatus(Long userId, Integer statusId, String statusTitle, Pageable pageable) {
        Page<Request> requests = requestRepository.findByUserIdAndStatus_IdStatusAndStatus_Title(userId, statusId, statusTitle, pageable);
        return requests.map(RequestDTO::toDTO);
    }

    @Override
    public RequestDTO addRequest(RequestDTO requestDTO) throws ResourceNotFoundException {
        // Retrieve the associated workflow process
        WorkflowProcess workflowProcess = processRepository.findById(requestDTO.getIdProcess())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workflow Process not found with id " + requestDTO.getIdProcess()
                ));

        // Convert DTO to entity and set the workflow process and userId
        Request request = RequestDTO.convertToEntity(requestDTO);
        request.setWorkflowProcess(workflowProcess);
        request.setUserId(requestDTO.getUserId());

        // Retrieve the 'New' status and set it in the request
        Status newStatus = statusRepository.findByTitle("new")
                .orElseThrow(() -> new ResourceNotFoundException("Status 'New' not found"));
        request.setStatus(newStatus);

        // Start the process instance using the process key and set the process instance ID
        String processKey = workflowProcess.getProcessKey();

        String processInstanceId = camundaService.startProcess(processKey);
        request.setProcessInstanceId(processInstanceId);

        // Save the request entity to the database
        Request savedRequest = requestRepository.save(request);

        // Generate PDF of the request data
        ByteArrayInputStream pdf = pdfGeneratorService.generatePdfForRequest(savedRequest);

        // Send email with the PDF as an attachment
        sendEmailWithPdf(pdf);

        // Convert the saved entity back to DTO and return it
        return convertToDto(savedRequest);
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
        requestDTO.setUserId(request.getUserId());
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
