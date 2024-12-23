package com.example.process.entity;



import com.example.process.DTO.WorkflowDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Workflow_Process")
public class WorkflowProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer  idProcess;
    @Column(nullable = false)
    private String Code;

    @Column(nullable = false)
    private String Title;

    @Column(nullable = false)
    private String ProcessKey;
    @Column(columnDefinition = "TEXT")
    private String diagramXML;
    @CreationTimestamp
    @Column(updatable = false, name = "date_added")
    private Date dateAdded;

    @ManyToMany
    @JoinTable(
            name = "workflow_process_status",
            joinColumns = @JoinColumn(name = "workflow_process_id"),
            inverseJoinColumns = @JoinColumn(name = "status_id")
    )
    private Set<Status> statuses;

    @Override
    public String toString() {
        return "WorkflowProcess{" +
                "idProcess=" + idProcess +
                ", Code='" + Code + '\'' +
                ", Title='" + Title + '\'' +
                ", ProcessKey='" + ProcessKey + '\'' +
                ", diagramXML='" + diagramXML + '\'' +
                ", dateAdded=" + dateAdded +
                ", statuses=" + statuses +
                '}';
    }
}
