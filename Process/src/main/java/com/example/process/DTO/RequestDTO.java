package com.example.process.DTO;

import com.example.process.entity.Request;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
public class RequestDTO {
    private Integer idRequest;
    private String fullName;
    private String object;
    private Long userId;
    private LocalDateTime addedDateRequest;
    private Integer idProcess;
    private String processInstanceId;
    private Integer statusId; // Existing status ID
    private String statusTitle;
    public static RequestDTO toDTO(Request entity) {
        RequestDTO dto = new RequestDTO();
        dto.setIdRequest(entity.getIdRequest());
        dto.setFullName(entity.getFullName());
        dto.setObject(entity.getObject());
        dto.setUserId(entity.getUserId());
        dto.setProcessInstanceId(entity.getProcessInstanceId());
        dto.setAddedDateRequest(entity.getAddedDateRequest());
        dto.setIdProcess(entity.getWorkflowProcess().getIdProcess());
        dto.setStatusId(Objects.nonNull(entity.getStatus())?entity.getStatus().getIdStatus():null);
        dto.setStatusTitle(Objects.nonNull(entity.getStatus())?entity.getStatus().getTitle():null);

        return dto;
    }
    public static  Request convertToEntity(RequestDTO requestDTO) {
        Request request = new Request();
        request.setFullName(requestDTO.getFullName());
        request.setObject(requestDTO.getObject());
        request.setUserId(requestDTO.getUserId());
        request.setAddedDateRequest(requestDTO.getAddedDateRequest());
        request.setProcessInstanceId(requestDTO.getProcessInstanceId());

        return request;
    }
}
