package com.gamascape.controller;

import com.gamascape.entity.LandListing;
import com.gamascape.service.LandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/land")
@RequiredArgsConstructor
public class LandController {

    private final LandService landService;

    @GetMapping
    public ResponseEntity<List<LandListing>> getAll() {
        return ResponseEntity.ok(landService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandListing> getById(@PathVariable Long id) {
        return ResponseEntity.ok(landService.getById(id));
    }
}