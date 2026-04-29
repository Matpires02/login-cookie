package com.matpires.login_cookie.repository;

import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(RoleName roleName);

    boolean existsByName(RoleName roleName);
}