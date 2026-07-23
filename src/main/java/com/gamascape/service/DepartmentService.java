package com.gamascape.service;

import jakarta.persistence.EntityNotFoundException;


import com.gamascape.entity.Department;
import com.gamascape.entity.DepartmentTask;
import com.gamascape.entity.User;
import com.gamascape.repository.DepartmentRepository;
import com.gamascape.repository.DepartmentTaskRepository;
import com.gamascape.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepo;
    private final DepartmentTaskRepository taskRepo;
    private final UserRepository userRepo;

    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }

    public Department createDepartment(Department department) {
        return departmentRepo.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepo.deleteById(id);
    }

    public Department updateDepartment(Long id, Department updated) {
        Department dept = departmentRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Dept not found"));
        dept.setName(updated.getName());
        return departmentRepo.save(dept);
    }

    public List<User> getTeamMembersByDepartment(Long deptId) {
        Department dept = departmentRepo.findById(deptId).orElseThrow(() -> new EntityNotFoundException("Dept not found"));
        return userRepo.findByRoleAndDepartment(User.Role.TEAM, dept);
    }

    public List<User> getAllTeamMembers() {
        return userRepo.findByRole(User.Role.TEAM);
    }

    public void assignMemberToDept(Long userId, Long deptId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (deptId == null) {
            user.setDepartment(null);
        } else {
            Department dept = departmentRepo.findById(deptId).orElseThrow(() -> new EntityNotFoundException("Dept not found"));
            user.setDepartment(dept);
        }
        userRepo.save(user);
    }

    public DepartmentTask createTask(DepartmentTask task) {
        if (task.getStatus() == null) task.setStatus("PENDING");
        return taskRepo.save(task);
    }

    public List<DepartmentTask> getTasksByMember(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return taskRepo.findByAssignedTo(user);
    }

    public List<DepartmentTask> getAllTasks() {
        return taskRepo.findAll();
    }

    public DepartmentTask updateTaskProgress(Long taskId, int progress, String status) {
        DepartmentTask task = taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setProgress(progress);
        task.setStatus(status);
        return taskRepo.save(task);
    }

    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }
}
