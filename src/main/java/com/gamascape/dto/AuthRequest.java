package com.gamascape.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}