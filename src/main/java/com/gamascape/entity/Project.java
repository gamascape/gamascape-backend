package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "projects") @Data
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String projectCode;

    @NotBlank(message = "Project name is required")
    private String name;

    @NotBlank(message = "Project location is required")
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime deadline;
    
    private int progress;
    private String status = "ACTIVE";
    private String imageEmoji;
    @Column(columnDefinition = "TEXT")
    private String backImageUrl;
    private boolean publiclyVisible = true; // Default true for now to show existing projects
    private LocalDateTime createdAt = LocalDateTime.now();

    private String estimateFileName;
    private String estimateFilePath;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("displayOrder ASC")
    private List<Phase> phases;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("postedAt DESC")
    private List<ProjectUpdate> updates;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Document> documents;
}