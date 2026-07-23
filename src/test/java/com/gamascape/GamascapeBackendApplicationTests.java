package com.gamascape;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test class to verify that the Spring Boot application
 * context loads successfully without errors.
 */
@SpringBootTest
class GamascapeBackendApplicationTests {

    /**
     * This test checks whether the Spring application
     * context starts correctly.
     */
    @org.springframework.beans.factory.annotation.Autowired
    private com.gamascape.repository.UserRepository userRepository;

    @Test
    void printUsers() {
        userRepository.findAll().forEach(user -> {
            System.out.println("USER_RECORD: Email=" + user.getEmail() + ", Role=" + user.getRole() + ", PasswordHash=" + user.getPassword());
        });
    }
}