package com.gamascape.repository;

import com.gamascape.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectCode(String code);
    List<Project> findByPubliclyVisibleTrue();
}