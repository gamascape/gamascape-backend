package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "client_projects") @Data
public class ClientProject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;

    private String unitLabel;
}