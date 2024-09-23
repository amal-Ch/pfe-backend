package com.example.process.entity;



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
@ToString(exclude = "requests")
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
    @CreationTimestamp
    @Column(updatable = false, name = "date_added")
    private Date dateAdded;

   /* @OneToMany(mappedBy = "workflowProcess", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Request> requests;
*/
}
