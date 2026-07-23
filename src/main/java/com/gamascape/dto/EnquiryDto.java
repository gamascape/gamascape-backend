package com.gamascape.dto;

import lombok.Data;

@Data
public class EnquiryDto {
    private String fullName;
    private String email;
    private String phone;
    private String interest;
    private String message;
}