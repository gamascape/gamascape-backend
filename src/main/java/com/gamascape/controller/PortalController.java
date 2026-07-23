package com.gamascape.controller;

import com.gamascape.dto.ProjectUpdateDto;
import com.gamascape.entity.*;
import com.gamascape.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PortalController {

    private final ProjectService projectService;
    private final com.gamascape.repository.UserRepository userRepo;
    private final com.gamascape.repository.ClientWorkRepository clientWorkRepo;
    private final com.gamascape.repository.WorkUpdateRepository workUpdateRepo;
    private final com.gamascape.repository.WorkDocumentRepository workDocumentRepo;

    @GetMapping("/api/portal/me")
    public ResponseEntity<User> getProfile(Authentication auth) {
        return ResponseEntity.ok(userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @PutMapping("/api/portal/me")
    public ResponseEntity<User> updateProfile(Authentication auth, @RequestBody User updated) {
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setName(updated.getName());
        return ResponseEntity.ok(userRepo.save(user));
    }

    // Public project listing (filtered for non-admin/team users)
    @GetMapping("/api/projects")
    public ResponseEntity<List<Project>> getAllProjects(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && 
            (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEAM")))) {
            return ResponseEntity.ok(projectService.getAllProjects());
        }
        return ResponseEntity.ok(projectService.getPublicProjects());
    }

    @GetMapping("/api/projects/{code}")
    public ResponseEntity<Project> getProject(@PathVariable String code) {
        return ResponseEntity.ok(projectService.getByCode(code));
    }

    // Client portal — my projects
    @GetMapping("/api/portal/my-projects")
    public ResponseEntity<List<ClientProject>> myProjects(Authentication auth) {
        return ResponseEntity.ok(projectService.getProjectsForUser(auth.getName()));
    }

    @GetMapping("/api/portal/projects/{id}/documents")
    public ResponseEntity<List<Document>> getDocs(@PathVariable Long id, Authentication auth) {
        if (!projectService.isUserAssignedToProject(auth.getName(), id)) {
            throw new AccessDeniedException("You do not have permission to view documents for this project.");
        }
        return ResponseEntity.ok(projectService.getDocuments(id));
    }

    // Team-only endpoints
    @PostMapping("/api/team/projects/{id}/updates")
    public ResponseEntity<ProjectUpdate> addUpdate(
            @PathVariable Long id,
            @RequestBody ProjectUpdateDto dto) {
        return ResponseEntity.ok(projectService.addUpdate(id, dto));
    }
    @PutMapping("/api/team/phases/{phaseId}")
    public ResponseEntity<Phase> updatePhase(
            @PathVariable Long phaseId,
            @RequestParam boolean isDone,
            @RequestParam boolean isCurrent) {
        return ResponseEntity.ok(projectService.updatePhase(phaseId, isDone, isCurrent));
    }

    // --- Client Work Portal Endpoints ---

    @GetMapping("/api/portal/my-works")
    public ResponseEntity<List<ClientWork>> getMyWorks(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(clientWorkRepo.findByClient(user));
    }

    @GetMapping("/api/portal/works/{workId}/updates")
    public ResponseEntity<List<WorkUpdate>> getWorkUpdates(@PathVariable Long workId, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        ClientWork work = clientWorkRepo.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found"));
        if (!work.getClient().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to view updates for this work.");
        }
        return ResponseEntity.ok(workUpdateRepo.findByClientWorkOrderByPostedAtDesc(work));
    }

    @GetMapping("/api/portal/works/{workId}/documents")
    public ResponseEntity<List<WorkDocument>> getWorkDocuments(@PathVariable Long workId, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        ClientWork work = clientWorkRepo.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found"));
        if (!work.getClient().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to view documents for this work.");
        }
        return ResponseEntity.ok(workDocumentRepo.findByClientWork(work));
    }
}