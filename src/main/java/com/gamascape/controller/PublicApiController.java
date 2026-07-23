package com.gamascape.controller;

import com.gamascape.entity.TeamMember;
import com.gamascape.entity.Project;
import com.gamascape.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicApiController {

    private final AdminService adminService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/team")
    public ResponseEntity<List<TeamMember>> getAllTeam() {
        return ResponseEntity.ok(adminService.getPublicTeam());
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getPublicProjects() {
        return ResponseEntity.ok(adminService.getPublicProjects());
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
        Path targetFolder = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = targetFolder.resolve(filename).normalize();

        if (!filePath.startsWith(targetFolder)) {
            return ResponseEntity.status(403).build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            contentType = "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            contentType = "image/gif";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}
