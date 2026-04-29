package com.matpires.login_cookie.service;

import com.matpires.login_cookie.dto.RegisterRequestDto;
import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.entity.User;
import com.matpires.login_cookie.enums.RoleName;
import com.matpires.login_cookie.repository.RoleRepository;
import com.matpires.login_cookie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Role> roles;

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            roles = Set.of(
                    roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Role USER não encontrada"))
            );
        } else {
            roles = dto.getRoles().stream()
                    .map(roleStr -> roleRepository.findByName(RoleName.valueOf(roleStr))
                            .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleStr)))
                    .collect(Collectors.toSet());
            // todo usuário tem um role_user
            roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role USER não encontrada")));
        }

        user.setRoles(roles);
        user.setActivated(true);

        userRepository.save(user);
    }
}
