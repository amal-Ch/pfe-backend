package com.example.process.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString(exclude = "workflowProcess")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Requests")
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRequest;
    private String fullName;
    private String object;
    @CreationTimestamp
    @Column(updatable = false, name = "date_added_Request")
    private LocalDateTime addedDateRequest;

    @ManyToOne
    @JoinColumn(name = "id_process", nullable = false)
    @JsonBackReference
    private WorkflowProcess workflowProcess;
    private String processInstanceId;
}
