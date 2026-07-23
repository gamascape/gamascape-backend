package com.gamascape.controller;

import com.gamascape.entity.Project;
import com.gamascape.entity.User;
import com.gamascape.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.List;
import com.gamascape.dto.AdminRegisterRequest;
import com.gamascape.dto.AdminResetPasswordRequest;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/register-team")
    public ResponseEntity<User> registerTeam(@Valid @RequestBody AdminRegisterRequest req) {
        return ResponseEntity.ok(adminService.registerTeamMember(req));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(adminService.updateUser(id, user));
    }

    @PutMapping("/users/{id}/toggle-block")
    public ResponseEntity<User> toggleBlock(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleBlock(id));
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<Void> adminResetPassword(@RequestBody AdminResetPasswordRequest req) {
        adminService.adminResetPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        return ResponseEntity.ok(adminService.addProject(project));
    }

    @PostMapping("/projects/{id}/estimate")
    public ResponseEntity<Project> uploadEstimate(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Project project = adminService.getProjectById(id);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        if (originalName == null || !isValidExtension(originalName)) {
            throw new RuntimeException("Invalid file type. Only secure documents and images are permitted.");
        }
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String storedName = "est_" + UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        project.setEstimateFileName(originalName);
        project.setEstimateFilePath(storedName);
        return ResponseEntity.ok(adminService.saveProject(project));
    }

    @PostMapping("/projects/{id}/back-image")
    public ResponseEntity<Project> uploadProjectBackImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Project project = adminService.getProjectById(id);

        Path uploadPath = Paths.get(uploadDir);
        System.out.println("Target upload directory: " + uploadPath.toAbsolutePath());
        if (!Files.exists(uploadPath)) {
            System.out.println("Creating directory...");
            Files.createDirectories(uploadPath);
        }
        System.out.println("Directory exists: " + Files.exists(uploadPath));
        System.out.println("Directory writable: " + Files.isWritable(uploadPath));

        String originalName = file.getOriginalFilename();
        System.out.println("Uploading project back image: " + originalName + " for project ID: " + id);
        
        if (originalName == null || !isValidExtension(originalName)) {
            throw new RuntimeException("Invalid file type. Only secure documents and images are permitted.");
        }
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String storedName = "p_back_" + UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(storedName);
        System.out.println("Saving file to: " + filePath.toAbsolutePath());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved successfully!");

        project.setBackImageUrl("/api/public/files/" + storedName);
        return ResponseEntity.ok(adminService.saveProject(project));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        adminService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return ResponseEntity.ok(adminService.updateProject(id, project));
    }

    @GetMapping("/land")
    public ResponseEntity<List<com.gamascape.entity.LandListing>> getAllLands() {
        return ResponseEntity.ok(adminService.getAllLands());
    }

    @PostMapping("/land")
    public ResponseEntity<com.gamascape.entity.LandListing> addLand(
            @RequestBody com.gamascape.entity.LandListing land) {
        return ResponseEntity.ok(adminService.addLand(land));
    }

    @PutMapping("/land/{id}")
    public ResponseEntity<com.gamascape.entity.LandListing> updateLand(@PathVariable Long id,
            @RequestBody com.gamascape.entity.LandListing land) {
        return ResponseEntity.ok(adminService.updateLand(id, land));
    }

    @PostMapping("/land/{id}/back-image")
    public ResponseEntity<com.gamascape.entity.LandListing> uploadLandBackImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        com.gamascape.entity.LandListing land = adminService.getLandById(id);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        if (originalName == null || !isValidExtension(originalName)) {
            throw new RuntimeException("Invalid file type. Only images are permitted.");
        }
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String storedName = "l_back_" + UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        land.setBackImageUrl("/api/public/files/" + storedName);
        return ResponseEntity.ok(adminService.updateLand(id, land));
    }

    @DeleteMapping("/land/{id}")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        adminService.deleteLand(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/team")
    public ResponseEntity<List<com.gamascape.entity.TeamMember>> getAllTeam() {
        return ResponseEntity.ok(adminService.getAllTeam());
    }

    @PostMapping("/team")
    public ResponseEntity<com.gamascape.entity.TeamMember> addTeamMember(
            @Valid @RequestBody com.gamascape.entity.TeamMember member) {
        return ResponseEntity.ok(adminService.addTeamMember(member));
    }

    @PutMapping("/team/{id}")
    public ResponseEntity<com.gamascape.entity.TeamMember> updateTeamMember(@PathVariable Long id,
            @Valid @RequestBody com.gamascape.entity.TeamMember member) {
        return ResponseEntity.ok(adminService.updateTeamMember(id, member));
    }

    @DeleteMapping("/team/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        adminService.deleteTeamMember(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects/{projectId}/assign/{clientId}")
    public ResponseEntity<Void> assignProject(
            @PathVariable Long projectId,
            @PathVariable Long clientId) {
        adminService.assignProjectToClient(projectId, clientId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clients/{clientId}/projects")
    public ResponseEntity<List<com.gamascape.entity.ClientProject>> getClientProjects(@PathVariable Long clientId) {
        return ResponseEntity.ok(adminService.getClientProjects(clientId));
    }

    // --- Client Work Management ---

    @GetMapping("/client-works")
    public ResponseEntity<List<com.gamascape.entity.ClientWork>> getAllClientWorks() {
        return ResponseEntity.ok(adminService.getAllClientWorks());
    }

    @PostMapping("/clients/{clientId}/works")
    public ResponseEntity<com.gamascape.entity.ClientWork> addClientWork(
            @PathVariable Long clientId,
            @Valid @RequestBody com.gamascape.entity.ClientWork work) {
        return ResponseEntity.ok(adminService.addClientWork(work, clientId));
    }

    @PutMapping("/client-works/{id}")
    public ResponseEntity<com.gamascape.entity.ClientWork> updateClientWork(
            @PathVariable Long id,
            @Valid @RequestBody com.gamascape.entity.ClientWork work) {
        return ResponseEntity.ok(adminService.updateClientWork(id, work));
    }

    @DeleteMapping("/client-works/{id}")
    public ResponseEntity<Void> deleteClientWork(@PathVariable Long id) {
        adminService.deleteClientWork(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/client-works/{workId}/updates")
    public ResponseEntity<com.gamascape.entity.WorkUpdate> addWorkUpdate(
            @PathVariable Long workId,
            @Valid @RequestBody com.gamascape.entity.WorkUpdate update) {
        return ResponseEntity.ok(adminService.addWorkUpdate(workId, update));
    }

    @PostMapping("/client-works/{workId}/documents")
    public ResponseEntity<com.gamascape.entity.WorkDocument> uploadWorkDocument(
            @PathVariable Long workId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        if (originalName == null || !isValidExtension(originalName)) {
            throw new RuntimeException("Invalid file type. Only secure documents and images are permitted.");
        }
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String storedName = "wrk_" + UUID.randomUUID() + extension;
        Files.copy(file.getInputStream(), uploadPath.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);

        com.gamascape.entity.WorkDocument doc = new com.gamascape.entity.WorkDocument();
        doc.setFileName(originalName);
        doc.setFilePath(storedName);
        doc.setFileType(file.getContentType());
        doc.setUploadedBy(uploadedBy);

        return ResponseEntity.ok(adminService.addWorkDocument(workId, doc));
    }

    @DeleteMapping("/work-documents/{id}")
    public ResponseEntity<Void> deleteWorkDocument(@PathVariable Long id) {
        adminService.deleteWorkDocument(id);
        return ResponseEntity.ok().build();
    }

    private boolean isValidExtension(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || lower.endsWith(".doc") || lower.endsWith(".docx") || 
               lower.endsWith(".xls") || lower.endsWith(".xlsx");
    }
}
