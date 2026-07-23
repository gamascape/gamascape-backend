package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "team_members") @Data
public class TeamMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;
    private String init;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private boolean publiclyVisible = true;
}
