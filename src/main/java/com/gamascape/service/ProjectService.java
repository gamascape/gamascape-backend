package com.gamascape.service;

import jakarta.persistence.EntityNotFoundException;


import com.gamascape.dto.ProjectUpdateDto;
import com.gamascape.entity.*;
import com.gamascape.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final ClientProjectRepository clientProjectRepo;
    private final ProjectUpdateRepository updateRepo;
    private final DocumentRepository docRepo;
    private final UserRepository userRepo;
    private final PhaseRepository phaseRepo;
    public List<Project> getAllProjects() { return projectRepo.findAll(); }

    public List<Project> getPublicProjects() { return projectRepo.findByPubliclyVisibleTrue(); }

    public Project getByCode(String code) {
        return projectRepo.findByProjectCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + code));
    }

    public List<ClientProject> getProjectsForUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return clientProjectRepo.findByUser(user);
    }

    @Transactional
    public ProjectUpdate addUpdate(Long projectId, ProjectUpdateDto dto) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        ProjectUpdate u = new ProjectUpdate();
        u.setProject(p);
        u.setMessage(dto.getMessage());
        u.setPostedBy(dto.getPostedBy());
        return updateRepo.save(u);
    }

    public boolean isUserAssignedToProject(String email, Long projectId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // Team and Admin bypass
        if (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.TEAM) return true;
        return clientProjectRepo.existsByUserAndProjectId(user, projectId);
    }

    public List<Document> getDocuments(Long projectId) {
        return docRepo.findByProjectId(projectId);
    }
    @Transactional
    public Phase updatePhase(Long phaseId, boolean isDone, boolean isCurrent) {
        Phase phase = phaseRepo.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Phase not found"));
        phase.setDone(isDone);
        phase.setCurrent(isCurrent);
        return phaseRepo.save(phase);
    }
}