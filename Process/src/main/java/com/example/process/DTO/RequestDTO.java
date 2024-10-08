package com.example.process.DTO;

import com.example.process.entity.Request;
import com.example.process.entity.WorkflowProcess;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
public class RequestDTO {
    private Integer idRequest;
    private String fullName;
    private String object;
    private LocalDateTime addedDateRequest;
    private Integer idProcess;
    private String processInstanceId;

    public static RequestDTO toDTO(Request entity) {
        RequestDTO dto = new RequestDTO();
        dto.setIdRequest(entity.getIdRequest());
        dto.setFullName(entity.getFullName());
        dto.setObject(entity.getObject());
        dto.setProcessInstanceId(entity.getProcessInstanceId());
        dto.setAddedDateRequest(entity.getAddedDateRequest());
        dto.setIdProcess(entity.getWorkflowProcess().getIdProcess());
        return dto;
    }
}
