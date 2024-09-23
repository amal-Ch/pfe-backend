package com.example.process.DTO;

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
}
