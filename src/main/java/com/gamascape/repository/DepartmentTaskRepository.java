package com.gamascape.repository;

import com.gamascape.entity.Department;
import com.gamascape.entity.DepartmentTask;
import com.gamascape.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentTaskRepository extends JpaRepository<DepartmentTask, Long> {
    List<DepartmentTask> findByAssignedTo(User user);
    List<DepartmentTask> findByDepartment(Department department);
}
