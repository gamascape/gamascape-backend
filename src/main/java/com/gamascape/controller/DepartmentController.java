package com.gamascape.controller;

import com.gamascape.entity.Department;
import com.gamascape.entity.DepartmentTask;
import com.gamascape.entity.User;
import com.gamascape.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // --- Admin Endpoints ---

    @GetMapping("/admin/departments")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping("/admin/departments")
    public Department createDepartment(@Valid @RequestBody Department dept) {
        return departmentService.createDepartment(dept);
    }

    @DeleteMapping("/admin/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/departments/{id}")
    public Department updateDepartment(@PathVariable Long id, @Valid @RequestBody Department dept) {
        return departmentService.updateDepartment(id, dept);
    }

    @GetMapping("/admin/departments/{id}/members")
    public List<User> getDepartmentMembers(@PathVariable Long id) {
        return departmentService.getTeamMembersByDepartment(id);
    }

    @GetMapping("/admin/team-members")
    public List<User> getAllTeamMembers() {
        return departmentService.getAllTeamMembers();
    }

    @PostMapping("/admin/assign-dept")
    public ResponseEntity<?> assignDept(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = payload.get("userId") != null ? Long.valueOf(payload.get("userId").toString()) : null;
            Long deptId = payload.get("deptId") != null ? Long.valueOf(payload.get("deptId").toString()) : null;
            departmentService.assignMemberToDept(userId, deptId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/admin/tasks")
    public DepartmentTask createTask(@Valid @RequestBody DepartmentTask task) {
        return departmentService.createTask(task);
    }

    @GetMapping("/admin/tasks")
    public List<DepartmentTask> getAllTasks() {
        return departmentService.getAllTasks();
    }

    @DeleteMapping("/admin/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        departmentService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    // --- Team Member Endpoints ---

    @GetMapping("/team/tasks/{userId}")
    public List<DepartmentTask> getMyTasks(@PathVariable Long userId) {
        return departmentService.getTasksByMember(userId);
    }

    @PutMapping("/team/tasks/{taskId}/progress")
    public DepartmentTask updateProgress(@PathVariable Long taskId, @RequestBody Map<String, Object> payload) {
        int progress = (int) payload.get("progress");
        String status = (String) payload.get("status");
        return departmentService.updateTaskProgress(taskId, progress, status);
    }
}
