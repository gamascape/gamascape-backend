package com.gamascape.dto;

import lombok.Data;

@Data
public class DepartmentAssignmentRequest {
    private Long userId;
    private Long deptId;
}
