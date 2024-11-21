package com.example.process.service.requestService;

import com.example.process.DTO.RequestDTO;
import com.example.process.DTO.StatusDto;
import com.example.process.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IServiceRequest {

     //void completeTaskWithNotification(String taskId, Map<String, Object> variables);
     void sendNotificationEmail(String decisionMessage);
     List<RequestDTO> getAllRequests();
    Page<RequestDTO> getRequestByUser(Pageable pageable,Long userId);
    Page<RequestDTO> getRequestsByStatus(Pageable pageable, Integer statusId, String statusTitle);

    Page<RequestDTO> getRequestsByUserAndStatus(Long userId, Integer statusId, String statusTitle, Pageable pageable);    RequestDTO addRequest(RequestDTO requestDTO);
   // public Request createRequestWithStatus(Request request, Integer statusId);
    //Request saveRequest(Request request);
    RequestDTO updateRequest(Integer id, RequestDTO requestDTO);
    Request updateRequestByProcessId(String processInstanceId, Integer statusId);
    void deleteRequest(Integer id);

    Page<RequestDTO> getAllPageRequest(Pageable pageable);
    Page<RequestDTO> searchRequests(String query,Pageable pageable);


}
