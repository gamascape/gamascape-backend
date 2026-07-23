package com.gamascape.repository;

import com.gamascape.entity.ProjectUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectUpdateRepository extends JpaRepository<ProjectUpdate, Long> {
    List<ProjectUpdate> findByProjectIdOrderByPostedAtDesc(Long projectId);
}