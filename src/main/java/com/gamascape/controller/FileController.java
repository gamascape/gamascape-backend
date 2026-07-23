package com.gamascape.controller;

import com.gamascape.entity.Document;
import com.gamascape.entity.Project;
import com.gamascape.repository.DocumentRepository;
import com.gamascape.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository docRepo;
    private final ProjectRepository projectRepo;
    private final com.gamascape.repository.WorkDocumentRepository workDocRepo;

    // View a work document (for images in portal)
    @GetMapping("/view/work-doc/{id}")
    public ResponseEntity<Resource> viewWorkDoc(@PathVariable Long id) throws MalformedURLException {
        com.gamascape.entity.WorkDocument doc = workDocRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        Path targetFolder = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = targetFolder.resolve(doc.getFilePath()).normalize();
        if (!filePath.startsWith(targetFolder)) return ResponseEntity.status(403).build();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) return ResponseEntity.notFound().build();

        String contentType = doc.getFileType() != null ? doc.getFileType() : "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    // Download a work document
    @GetMapping("/download/work-doc/{id}")
    public ResponseEntity<Resource> downloadWorkDoc(@PathVariable Long id) throws MalformedURLException {
        com.gamascape.entity.WorkDocument doc = workDocRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        Path targetFolder = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = targetFolder.resolve(doc.getFilePath()).normalize();
        if (!filePath.startsWith(targetFolder)) return ResponseEntity.status(403).build();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    // Upload a document for a project (TEAM/ADMIN only)
    @PostMapping("/upload/{projectId}")
    public ResponseEntity<Document> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy) throws IOException {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        // Generate unique filename to avoid collisions
        String originalName = file.getOriginalFilename();
        String extension    = originalName != null && originalName.contains(".")
                              ? originalName.substring(originalName.lastIndexOf("."))
                              : "";
        String storedName   = UUID.randomUUID() + extension;
        Path   filePath     = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save document record
        Document doc = new Document();
        doc.setProject(project);
        doc.setFileName(originalName);
        doc.setFilePath(storedName);
        doc.setUploadedBy(uploadedBy);
        return ResponseEntity.ok(docRepo.save(doc));
    }

    // Download a document by ID
    @GetMapping("/download/{docId}")
    public ResponseEntity<Resource> download(@PathVariable Long docId)
            throws MalformedURLException {

        Document doc = docRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Path targetFolder = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = targetFolder.resolve(doc.getFilePath()).normalize();
        if (!filePath.startsWith(targetFolder)) return ResponseEntity.status(403).build();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    // Download an estimate for a project
    @GetMapping("/download/estimate/{projectId}")
    public ResponseEntity<Resource> downloadEstimate(@PathVariable Long projectId)
            throws MalformedURLException {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (project.getEstimateFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        Path targetFolder = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = targetFolder.resolve(project.getEstimateFilePath()).normalize();
        if (!filePath.startsWith(targetFolder)) return ResponseEntity.status(403).build();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + project.getEstimateFileName() + "\"")
                .body(resource);
    }
}