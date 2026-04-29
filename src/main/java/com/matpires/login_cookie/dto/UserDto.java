package com.matpires.login_cookie.dto;

import com.matpires.login_cookie.entity.User;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link User}
 */
public record UserDto(UUID id,
                      @NotNull(message = "E-mail requerido") @Email(message = "E-mail inválido") @NotEmpty(message = "E-mail requerido") @NotBlank(message = "E-mail requerido") String email,
                      @NotNull(message = "Password requerido") @Size(message = "Password deve ser maior que 8", min = 8) String password,
                      Boolean activated, Set<String> roles) implements Serializable {
}