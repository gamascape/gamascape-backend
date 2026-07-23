package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity @Table(name = "land_listings") @Data
public class LandListing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Land name is required")
    private String name;
    
    @NotBlank(message = "Area is required")
    private String area;
    
    @NotBlank(message = "Price is required")
    private String price;
    
    private int healthScore;
    
    @NotBlank(message = "Location is required")
    private String location;
    private String imageEmoji;
    @Column(columnDefinition = "TEXT")
    private String backImageUrl;
    private String tags;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}