package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users") @Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CLIENT;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String resetOtp;
    private LocalDateTime resetOtpExpiry;
    private Integer resetOtpAttempts = 0;

    private boolean blocked = false;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public enum Role { CLIENT, TEAM, ADMIN }
}