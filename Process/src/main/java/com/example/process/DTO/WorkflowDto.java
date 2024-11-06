package com.example.process.DTO;

import com.example.process.entity.WorkflowProcess;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class WorkflowDto {
    private Integer idProcess;
    private String code;
    private String title;
    private String processKey;

    private Date dateAdded;
    public static WorkflowDto toDTO(WorkflowProcess entity) {
        WorkflowDto dto = new WorkflowDto();
        dto.setIdProcess(entity.getIdProcess());
        dto.setCode(entity.getCode());
        dto.setTitle(entity.getTitle());
        dto.setProcessKey(entity.getProcessKey());
        dto.setDateAdded(entity.getDateAdded());
        return dto;
    }

    // Convert from DTO to Entity
    public static WorkflowProcess toEntity(WorkflowDto dto) {
        WorkflowProcess entity = new WorkflowProcess();
        entity.setIdProcess(dto.getIdProcess());
        entity.setCode(dto.getCode());
        entity.setTitle(dto.getTitle());
        entity.setProcessKey(dto.getProcessKey());
        entity.setDateAdded(dto.getDateAdded());
        return entity;
    }
}
