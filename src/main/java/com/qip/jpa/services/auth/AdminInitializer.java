package com.qip.jpa.services.auth;

import com.qip.jpa.entities.Role;
import com.qip.jpa.entities.User;
import com.qip.jpa.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "admin@qip.com.ar";

            // Verifica si ya existe un admin con ese email
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = new User();
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode("admin123")); // ¡Nunca uses contraseñas débiles en producción!
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
                System.out.println("✔ Admin creado: " + email);
            } else {
                System.out.println("ℹ Ya existe un admin registrado: " + email);
            }
        };
    }
}

