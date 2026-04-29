package com.matpires.login_cookie.dto;

import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.enums.RoleName;

import java.io.Serializable;

/**
 * DTO for {@link Role}
 */
public record RoleDto(Long id, RoleName name) implements Serializable {
}