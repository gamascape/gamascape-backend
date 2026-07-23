package com.gamascape.controller;

import com.gamascape.dto.EnquiryDto;
import com.gamascape.entity.Enquiry;
import com.gamascape.service.EnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/enquiry")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<Enquiry> submit(@Valid @RequestBody EnquiryDto dto) {
        return ResponseEntity.ok(enquiryService.submit(dto));
    }

    @GetMapping
    public ResponseEntity<List<Enquiry>> getAll() {
        return ResponseEntity.ok(enquiryService.getAll());
    }

    @PutMapping("/{id}/seen")
    public ResponseEntity<Enquiry> markAsSeen(@PathVariable Long id) {
        return ResponseEntity.ok(enquiryService.markAsSeen(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable Long id) {
        enquiryService.deleteEnquiry(id);
        return ResponseEntity.ok().build();
    }
}