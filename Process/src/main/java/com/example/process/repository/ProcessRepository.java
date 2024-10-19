package com.example.process.repository;

import com.example.process.entity.WorkflowProcess;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<WorkflowProcess, Integer> {
    @Query("SELECT w FROM WorkflowProcess w WHERE :search IS NULL OR w.Title LIKE %:search%")
    Page<WorkflowProcess> searchByTitle(@Param("search") String search, Pageable pageable);

}
