package com.example.process.repository;

import com.example.process.entity.WorkflowProcess;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<WorkflowProcess, Integer> {
    @Query("SELECT w FROM WorkflowProcess w WHERE LOWER(w.Title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<WorkflowProcess> searchByTitle(@Param("query") String query, Pageable pageable);

}
