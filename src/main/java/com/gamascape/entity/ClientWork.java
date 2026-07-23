package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "client_works")
@Data
public class ClientWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String workCode;
    
    @NotBlank(message = "Work name is required")
    private String name;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime deadline;
    private int progress;
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @OneToMany(mappedBy = "clientWork", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("postedAt DESC")
    private List<WorkUpdate> updates;

    @OneToMany(mappedBy = "clientWork", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WorkDocument> documents;

    private LocalDateTime createdAt = LocalDateTime.now();
}
