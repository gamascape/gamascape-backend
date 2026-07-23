package com.gamascape.repository;

import com.gamascape.entity.Department;
import com.gamascape.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
    List<User> findByRoleAndDepartment(User.Role role, Department department);
}