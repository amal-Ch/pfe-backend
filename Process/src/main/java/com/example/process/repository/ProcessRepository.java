package com.example.process.repository;

import com.example.process.entity.WorkflowProcess;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends JpaRepository<WorkflowProcess, Integer> {
    @Query(value = "SELECT EXTRACT(YEAR FROM dateAdded) AS year, EXTRACT(MONTH FROM dateAdded) AS month, COUNT(*) AS workflow_count " +
            "FROM WorkflowProcess GROUP BY EXTRACT(YEAR FROM dateAdded),EXTRACT(MONTH FROM dateAdded)" +
            "ORDER BY EXTRACT(YEAR FROM dateAdded) DESC, EXTRACT(MONTH FROM dateAdded) DESC")
    List<Object[]> findWorkflowCountGroupedByYearAndMonth();

    @Query("SELECT w FROM WorkflowProcess w WHERE :search IS NULL OR w.Title LIKE %:search%")
    Page<WorkflowProcess> searchByTitle(@Param("search") String search, Pageable pageable);

}
