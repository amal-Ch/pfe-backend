package com.example.process.repository;

import com.example.process.entity.Request;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Integer> {
    @Query("SELECT r FROM Request r WHERE LOWER(r.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Request> searchByFullName(@Param("query") String query, Pageable pageable);

}
