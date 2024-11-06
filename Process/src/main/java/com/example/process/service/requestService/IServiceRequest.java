package com.example.process.service.requestService;

import com.example.process.DTO.RequestDTO;
import com.example.process.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IServiceRequest {

     List<RequestDTO> getAllRequests();

    RequestDTO addRequest(RequestDTO requestDTO);
   // public Request createRequestWithStatus(Request request, Integer statusId);
    //Request saveRequest(Request request);
    RequestDTO updateRequest(Integer id, RequestDTO requestDTO);
    void updateStatusByProcessId(String processInstanceId,Integer idStatus);
     void deleteRequest(Integer id);

    Page<RequestDTO> getAllPageRequest(Pageable pageable);
    Page<RequestDTO> searchRequests(String query,Pageable pageable);


}
