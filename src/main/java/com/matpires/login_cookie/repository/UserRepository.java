package com.matpires.login_cookie.repository;

import com.matpires.login_cookie.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@Email @NotBlank String email);

    Optional<User> findByEmailAndActivatedTrue(@Email @NotBlank String email);
}