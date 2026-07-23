package com.gamascape.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials")
@Data
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String content;

    private String role; // e.g., "Developer", "Client"

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating; // 1-5

    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Note: The database appears to have both 'active' and 'is_active' columns as NOT NULL.
    // We map to both to satisfy constraints during insertion in local environments.
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "active", nullable = false)
    private boolean activeLegacy = true;
    
    // Keep them in sync
    public void setActive(boolean active) {
        this.active = active;
        this.activeLegacy = active;
    }
}
