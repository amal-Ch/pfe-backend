package com.example.process.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusDto {
    private Integer statusId;
    private String processInstanceId;
    private String statusTitle;
}
