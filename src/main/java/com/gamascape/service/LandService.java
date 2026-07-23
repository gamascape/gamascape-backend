package com.gamascape.service;

import jakarta.persistence.EntityNotFoundException;


import com.gamascape.entity.LandListing;
import com.gamascape.repository.LandListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandService {

    private final LandListingRepository repo;

    public List<LandListing> getAllActive() { return repo.findByActiveTrue(); }

    public LandListing getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    public LandListing save(LandListing l) { return repo.save(l); }
}