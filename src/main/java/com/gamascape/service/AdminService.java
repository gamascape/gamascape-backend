package com.gamascape.service;

import jakarta.persistence.EntityNotFoundException;


import com.gamascape.dto.AdminRegisterRequest;
import com.gamascape.dto.AdminResetPasswordRequest;
import com.gamascape.entity.Project;
import com.gamascape.entity.User;
import com.gamascape.repository.ProjectRepository;
import com.gamascape.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;
    private final ProjectRepository projectRepo;
    private final com.gamascape.repository.LandListingRepository landRepo;
    private final com.gamascape.repository.TeamMemberRepository teamRepo;
    private final com.gamascape.repository.ClientProjectRepository clientProjectRepo;
    private final com.gamascape.repository.ClientWorkRepository clientWorkRepo;
    private final com.gamascape.repository.WorkUpdateRepository workUpdateRepo;
    private final com.gamascape.repository.WorkDocumentRepository workDocumentRepo;

    @jakarta.annotation.PostConstruct
    @org.springframework.transaction.annotation.Transactional
    public void cleanupDemoData() {
        // 1. Migrate codes (existing logic)
        migrateWorkCodes();

        // 2. Remove "The Azure Villas" demo project and its assignments
        List<Project> demoProjects = projectRepo.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase("The Azure Villas"))
                .toList();
        
        for (Project p : demoProjects) {
            // Delete assignments first if cascade doesn't handle it
            List<com.gamascape.entity.ClientProject> assignments = clientProjectRepo.findAll().stream()
                .filter(cp -> cp.getProject().getId().equals(p.getId()))
                .toList();
            clientProjectRepo.deleteAll(assignments);
            
            // Delete project (cascade should handle documents)
            projectRepo.delete(p);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    protected void migrateWorkCodes() {
        List<com.gamascape.entity.ClientWork> all = clientWorkRepo.findAll();
        // Sort by ID to maintain original order
        all.sort(java.util.Comparator.comparing(com.gamascape.entity.ClientWork::getId));
        
        long nextCode = 101;
        for (com.gamascape.entity.ClientWork work : all) {
            String currentCode = work.getWorkCode();
            // If it's the old long format or null, update it
            if (currentCode == null || currentCode.startsWith("WRK-") || currentCode.length() > 5) {
                work.setWorkCode(String.valueOf(nextCode++));
                clientWorkRepo.saveAndFlush(work);
            } else {
                // If it's already numeric, keep it and update nextCode to avoid duplicates
                try {
                    long currentVal = Long.parseLong(currentCode);
                    if (currentVal >= nextCode) {
                        nextCode = currentVal + 1;
                    }
                } catch (NumberFormatException e) {
                    // Not a number, treat as old/invalid format
                    work.setWorkCode(String.valueOf(nextCode++));
                    clientWorkRepo.saveAndFlush(work);
                }
            }
        }
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public List<Project> getPublicProjects() {
        return projectRepo.findByPubliclyVisibleTrue();
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    public User registerTeamMember(AdminRegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new EntityNotFoundException("Email already exists");
        }
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(User.Role.TEAM);
        return userRepo.save(user);
    }

    public void adminResetPassword(AdminResetPasswordRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    public User updateUser(Long id, User updated) {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        existing.setName(updated.getName());
        return userRepo.save(existing);
    }

    public User toggleBlock(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBlocked(!user.isBlocked());
        return userRepo.save(user);
    }

    public Project addProject(Project project) {
        // Basic unique code generation if not provided
        if (project.getProjectCode() == null || project.getProjectCode().isEmpty()) {
            List<Project> all = projectRepo.findAll();
            long max = 100;
            for (Project p : all) {
                try {
                    String code = p.getProjectCode();
                    if (code != null && code.startsWith("PRJ-")) {
                        long val = Long.parseLong(code.substring(4));
                        if (val > max) max = val;
                    }
                } catch (Exception e) {}
            }
            project.setProjectCode("PRJ-" + (max + 1));
        }
        return projectRepo.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public Project saveProject(Project project) {
        return projectRepo.save(project);
    }

    public void deleteProject(Long id) {
        projectRepo.deleteById(id);
    }

    public Project updateProject(Long id, Project updated) {
        Project existing = getProjectById(id);
        existing.setName(updated.getName());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        existing.setDeadline(updated.getDeadline());
        existing.setProgress(updated.getProgress());
        existing.setStatus(updated.getStatus());
        existing.setImageEmoji(updated.getImageEmoji());
        existing.setBackImageUrl(updated.getBackImageUrl());
        return projectRepo.save(existing);
    }

    public void assignProjectToClient(Long projectId, Long clientId) {
        Project project = getProjectById(projectId);
        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        
        if (client.getRole() != User.Role.CLIENT) {
            throw new EntityNotFoundException("User is not a client");
        }

        // Check if already assigned
        boolean alreadyAssigned = clientProjectRepo.findByUser(client).stream()
                .anyMatch(cp -> cp.getProject().getId().equals(projectId));
        
        if (!alreadyAssigned) {
            com.gamascape.entity.ClientProject cp = new com.gamascape.entity.ClientProject();
            cp.setProject(project);
            cp.setUser(client);
            cp.setUnitLabel("Client Project"); // Default label
            clientProjectRepo.save(cp);
        }
    }

    public List<com.gamascape.entity.ClientProject> getClientProjects(Long clientId) {
        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientProjectRepo.findByUser(client);
    }

    public List<com.gamascape.entity.LandListing> getAllLands() {
        return landRepo.findAll();
    }

    public com.gamascape.entity.LandListing addLand(com.gamascape.entity.LandListing land) {
        return landRepo.save(land);
    }

    public com.gamascape.entity.LandListing getLandById(Long id) {
        return landRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Land not found"));
    }

    public void deleteLand(Long id) {
        landRepo.deleteById(id);
    }

    public com.gamascape.entity.LandListing updateLand(Long id, com.gamascape.entity.LandListing updated) {
        com.gamascape.entity.LandListing existing = landRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Land not found"));
        existing.setName(updated.getName());
        existing.setArea(updated.getArea());
        existing.setPrice(updated.getPrice());
        existing.setHealthScore(updated.getHealthScore());
        existing.setLocation(updated.getLocation());
        existing.setImageEmoji(updated.getImageEmoji());
        existing.setBackImageUrl(updated.getBackImageUrl());
        existing.setTags(updated.getTags());
        existing.setActive(updated.isActive());
        return landRepo.save(existing);
    }

    public List<com.gamascape.entity.TeamMember> getAllTeam() {
        return teamRepo.findAll();
    }

    public List<com.gamascape.entity.TeamMember> getPublicTeam() {
        return teamRepo.findByPubliclyVisibleTrue();
    }

    public com.gamascape.entity.TeamMember addTeamMember(com.gamascape.entity.TeamMember member) {
        return teamRepo.save(member);
    }

    public com.gamascape.entity.TeamMember updateTeamMember(Long id, com.gamascape.entity.TeamMember updated) {
        com.gamascape.entity.TeamMember existing = teamRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));
        existing.setName(updated.getName());
        existing.setRole(updated.getRole());
        existing.setInit(updated.getInit());
        existing.setBio(updated.getBio());
        existing.setPubliclyVisible(updated.isPubliclyVisible());
        return teamRepo.save(existing);
    }

    public void deleteTeamMember(Long id) {
        teamRepo.deleteById(id);
    }

    // --- Client Work Management ---

    public List<com.gamascape.entity.ClientWork> getAllClientWorks() {
        return clientWorkRepo.findAll();
    }

    public com.gamascape.entity.ClientWork addClientWork(com.gamascape.entity.ClientWork work, Long clientId) {
        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        if (client.getRole() != User.Role.CLIENT) {
            throw new EntityNotFoundException("User must be a CLIENT");
        }
        if (work.getWorkCode() == null || work.getWorkCode().isEmpty()) {
            List<com.gamascape.entity.ClientWork> all = clientWorkRepo.findAll();
            long max = 100;
            for (com.gamascape.entity.ClientWork w : all) {
                try {
                    // Try to parse existing code as numeric
                    long val = Long.parseLong(w.getWorkCode());
                    if (val > max) max = val;
                } catch (Exception e) {
                    // Ignore codes that aren't purely numeric (like the old WRK- timestamp ones)
                }
            }
            work.setWorkCode(String.valueOf(max + 1));
        }
        work.setClient(client);
        
        if (work.getAssignedTo() != null && work.getAssignedTo().getId() != null) {
            User assigned = userRepo.findById(work.getAssignedTo().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
            work.setAssignedTo(assigned);
        }
        
        return clientWorkRepo.save(work);
    }

    public com.gamascape.entity.ClientWork updateClientWork(Long id, com.gamascape.entity.ClientWork updated) {
        com.gamascape.entity.ClientWork existing = clientWorkRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client Work not found"));
        existing.setName(updated.getName());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        existing.setDeadline(updated.getDeadline());
        existing.setProgress(updated.getProgress());
        existing.setStatus(updated.getStatus());
        
        // Handle optional assignment
        if (updated.getAssignedTo() != null && updated.getAssignedTo().getId() != null) {
            User assigned = userRepo.findById(updated.getAssignedTo().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found"));
            existing.setAssignedTo(assigned);
        } else {
            existing.setAssignedTo(null);
        }
        
        return clientWorkRepo.save(existing);
    }

    public List<com.gamascape.entity.ClientWork> getWorksForTeamMember(Long teamId) {
        User team = userRepo.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));
        return clientWorkRepo.findByAssignedTo(team);
    }

    public void deleteClientWork(Long id) {
        clientWorkRepo.deleteById(id);
    }

    public com.gamascape.entity.WorkUpdate addWorkUpdate(Long workId, com.gamascape.entity.WorkUpdate update) {
        com.gamascape.entity.ClientWork work = clientWorkRepo.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Client Work not found"));
        update.setClientWork(work);
        return workUpdateRepo.save(update);
    }

    public com.gamascape.entity.WorkDocument addWorkDocument(Long workId, com.gamascape.entity.WorkDocument doc) {
        com.gamascape.entity.ClientWork work = clientWorkRepo.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Client Work not found"));
        doc.setClientWork(work);
        return workDocumentRepo.save(doc);
    }

    public List<com.gamascape.entity.ClientWork> getWorksForClient(Long clientId) {
        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientWorkRepo.findByClient(client);
    }

    public void deleteWorkDocument(Long id) {
        workDocumentRepo.deleteById(id);
    }
}
