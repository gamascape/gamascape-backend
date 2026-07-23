package com.gamascape.repository;

import com.gamascape.entity.ClientProject;
import com.gamascape.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientProjectRepository extends JpaRepository<ClientProject, Long> {
    List<ClientProject> findByUser(User user);
    boolean existsByUserAndProjectId(User user, Long projectId);
}