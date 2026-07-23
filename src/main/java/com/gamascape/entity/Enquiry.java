package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Entity @Table(name = "enquiries") @Data
public class Enquiry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    private String phone;
    private String interest;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String status = "NEW";
    private LocalDateTime submittedAt = LocalDateTime.now();
}