package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity @Table(name = "project_updates") @Data
public class ProjectUpdate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "project_id") @JsonIgnore
    private Project project;

    @NotBlank(message = "Message is required")
    @Column(columnDefinition = "TEXT")
    private String message;

    @NotBlank(message = "Posted by is required")
    private String postedBy;
    private LocalDateTime postedAt = LocalDateTime.now();
}