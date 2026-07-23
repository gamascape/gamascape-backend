package com.gamascape.dto;

import lombok.Data;

@Data
public class AdminResetPasswordRequest {
    private String email;
    private String newPassword;
}
