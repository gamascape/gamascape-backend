package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity @Table(name = "documents") @Data
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "project_id") @JsonIgnore
    private Project project;

    private String fileName;
    private String filePath;
    private String uploadedBy;
    private LocalDateTime uploadedAt = LocalDateTime.now();
}