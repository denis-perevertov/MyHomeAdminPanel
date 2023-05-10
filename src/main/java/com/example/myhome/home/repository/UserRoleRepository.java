package com.example.myhome.home.repository;

import com.example.myhome.home.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByName(String name);
    List<UserRole> findAllByName(String name);

    @Query("SELECT m FROM UserRole m WHERE m.master=true")
    List<UserRole> findAllMasterRoles();

    @Query("SELECT m FROM UserRole m WHERE m.master=false")
    List<UserRole> findAllManagerRoles();
}
