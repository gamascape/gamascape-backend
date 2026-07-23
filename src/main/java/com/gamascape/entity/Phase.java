package com.gamascape.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

@Entity @Table(name = "phases") @Data
public class Phase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "project_id") @JsonIgnore
    private Project project;

    @NotBlank(message = "Phase name is required")
    private String phaseName;
    private boolean isDone;
    private boolean isCurrent;
    
    @NotBlank(message = "Phase date is required")
    private String phaseDate;
    private String notes;
    private int displayOrder;

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public void setCurrent(boolean current) {
        this.isCurrent = current;
    }
}