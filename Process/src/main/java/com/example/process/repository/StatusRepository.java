package com.example.process.repository;

import com.example.process.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    Optional<Status> findByTitle(String title);
    Optional<Status> findById(Integer idStatus);
}
