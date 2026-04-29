package com.matpires.login_cookie.mapper;

import com.matpires.login_cookie.dto.UserDto;
import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.enums.RoleName;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "roles",
            expression = "java(mapRolesToString(user.getRoles()))")
    UserDto toDTO(User user);

    @Mapping(target = "roles",
            expression = "java(mapStringsToRoles(dto.roles()))")
    User toEntity(UserDto dto);

    // 👇 ENTITY → DTO
    default Set<String> mapRolesToString(Set<Role> roles) {
        if (roles == null) return null;

        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    // 👇 DTO → ENTITY
    default Set<Role> mapStringsToRoles(Set<String> roles) {
        if (roles == null) return null;

        return roles.stream()
                .map(roleStr -> {
                    Role role = new Role();
                    role.setName(RoleName.valueOf(roleStr));
                    return role;
                })
                .collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);
}