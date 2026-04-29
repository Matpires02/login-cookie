package com.matpires.login_cookie.config;

import com.matpires.login_cookie.entity.Role;
import com.matpires.login_cookie.enums.RoleName;
import com.matpires.login_cookie.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class InitDataConfiguration implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public InitDataConfiguration(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Init data configuration");

        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        });

        log.info("Finish data configuration");
    }
}
