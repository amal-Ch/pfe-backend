package com.example.process.repository;

import com.example.process.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Integer> {

    @Query("SELECT r FROM Request r WHERE :search IS NULL OR r.fullName LIKE %:search%")
    Page<Request> searchByFullName(@Param("search") String search, Pageable pageable);
}
