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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Integer> {
   // @Query(" SELECT r.addedDateRequest AS request_date FROM Request r ORDER BY r.addedDateRequest DESC")
//   @Query(value = "SELECT DATE(r.addedDateRequest) AS request_date, COUNT(*) AS request_count" +
//           " FROM Request r GROUP BY DATE(r.addedDateRequest) ORDER BY DATE(r.addedDateRequest) DESC")
   @Query(value = " SELECT DATE(r.date_added_request) AS request_date, COUNT(*) AS request_count FROM requests r " +
           "GROUP BY DATE(r.date_added_request) ORDER BY DATE(r.date_added_request) DESC", nativeQuery = true)
    List<Object[]> findRequestCountGroupedByDate();

    @Query(value = "SELECT s.title, COUNT(r.idRequest) FROM Request  r JOIN Status s ON r.status.idStatus = s.idStatus GROUP BY s.title")
    List<Object[]> countRequestsByStatusTitle();
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
