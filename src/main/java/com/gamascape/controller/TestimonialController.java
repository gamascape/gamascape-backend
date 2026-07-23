package com.gamascape.controller;

import com.gamascape.entity.Testimonial;
import com.gamascape.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;

    @GetMapping
    public ResponseEntity<List<Testimonial>> getAllTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Testimonial>> getActiveTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findByActiveTrueOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<Testimonial> addTestimonial(@Valid @RequestBody Testimonial testimonial) {
        return ResponseEntity.ok(testimonialRepository.save(testimonial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
        if (!testimonialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        testimonialRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Testimonial> toggleActive(@PathVariable Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
        testimonial.setActive(!testimonial.isActive());
        return ResponseEntity.ok(testimonialRepository.save(testimonial));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Testimonial> updateTestimonial(@PathVariable Long id, @Valid @RequestBody Testimonial updated) {
        Testimonial existing = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
        existing.setName(updated.getName());
        existing.setContent(updated.getContent());
        existing.setRole(updated.getRole());
        existing.setRating(updated.getRating());
        existing.setActive(updated.isActive());
        return ResponseEntity.ok(testimonialRepository.save(existing));
    }
}
