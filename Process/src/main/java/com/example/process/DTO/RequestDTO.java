package com.example.process.DTO;

import com.example.process.entity.Request;
import com.example.process.entity.RequestStatus;
import com.example.process.entity.WorkflowProcess;
import lombok.*;

import java.time.LocalDateTime;


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
    private RequestStatus status;

    public static RequestDTO toDTO(Request entity) {
        RequestDTO dto = new RequestDTO();
        dto.setIdRequest(entity.getIdRequest());
        dto.setFullName(entity.getFullName());
        dto.setObject(entity.getObject());
        dto.setUserId(entity.getUserId());
        dto.setProcessInstanceId(entity.getProcessInstanceId());
        dto.setAddedDateRequest(entity.getAddedDateRequest());
        dto.setIdProcess(entity.getWorkflowProcess().getIdProcess());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
