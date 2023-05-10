package com.example.myhome.home.repository;

import com.example.myhome.home.model.Admin;
import com.example.myhome.util.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {

    List<Admin> getAdminsByRole(UserRole role);
    Optional<Admin> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(value="SELECT a FROM Admin a WHERE a.first_name LIKE %:name% OR a.last_name LIKE %:name%")
    Page<Admin> findByName(String name, Pageable pageable);

}
