package com.example.process.repository;

import com.example.process.entity.Request;
import com.example.process.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Integer> {
    Page<Request> findByUserId(Pageable pageable ,Long userId);
    Page<Request> findByStatus_IdStatusAndStatus_Title(Pageable pageable, Integer idStatus, String title);

    Page<Request> findByUserIdAndStatus_IdStatusAndStatus_Title(Long userId, Integer idStatus, String title, Pageable pageable);
    Optional <Request> findByProcessInstanceId(String processInstanceId);

    @Modifying
    @Query("update Request r set r.status = :status where r.processInstanceId = :processInstanceId")
    void updateStatusByProcessInstanceId(@Param("status") Status status, @Param("processInstanceId") String processInstanceId);

    @Query("SELECT r FROM Request r WHERE :search IS NULL OR r.fullName LIKE %:search%")
    Page<Request> searchByFullName(@Param("search") String search, Pageable pageable);
}
