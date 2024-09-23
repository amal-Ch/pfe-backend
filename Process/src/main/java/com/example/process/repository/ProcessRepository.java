package com.example.process.repository;

import com.example.process.entity.WorkflowProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<WorkflowProcess, Integer> {
}
