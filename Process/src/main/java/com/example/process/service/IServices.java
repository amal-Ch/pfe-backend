package com.example.process.service;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.WorkflowDto;
import com.example.process.entity.Request;
import com.example.process.entity.WorkflowProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IServices {
    ///////////////////PROCESSES///////////////////////
    List<WorkflowProcess> getAllProcesses();
    Optional<WorkflowProcess> getProcessById(Integer id);
    WorkflowProcess createProcess(WorkflowProcess process);
    WorkflowProcess updateProcess(Integer id, WorkflowProcess processDetails);
    void deleteProcess(Integer id);

///////////////////REQUESTES///////////////////////
     List<RequestDTO> getAllRequests();

    RequestDTO addRequest(RequestDTO requestDTO);
    Request saveRequest(Request request);
    RequestDTO updateRequest(Integer id, RequestDTO requestDTO);
     void deleteRequest(Integer id);
    Page<WorkflowDto> getAllPage(Pageable pageable);
}
