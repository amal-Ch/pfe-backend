package com.example.administration.repository;

import com.example.administration.entity.ERole;
import com.example.administration.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(value = "SELECT r.name, COUNT(ur.user_id) FROM roles r " +
            "LEFT JOIN user_roles ur ON r.id = ur.role_id " +
            "GROUP BY r.name", nativeQuery = true)
    List<Object[]> findRoleUserCounts();
    Optional<Role> findByName(ERole name);
}