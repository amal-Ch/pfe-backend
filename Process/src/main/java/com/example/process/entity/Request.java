package com.example.process.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
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

    @Column(name = "user_id")
    private Long userId; // This will store the user ID from the other microservice

    // New field for request status


    @ManyToOne
    @JoinColumn(name = "id_process", nullable = false)
    @JsonBackReference
    private WorkflowProcess workflowProcess;

    private String processInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = true) // nullable if a Request can exist without a Status
    private Status status;

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Request{" +
                "idRequest=" + idRequest +
                ", fullName='" + fullName + '\'' +
                ", object='" + object + '\'' +
                ", addedDateRequest=" + addedDateRequest +
                ", userId=" + userId +
                ", workflowProcess=" + workflowProcess +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", status=" + status +
                '}';
    }
}
