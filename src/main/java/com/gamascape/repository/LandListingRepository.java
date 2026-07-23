package com.gamascape.repository;

import com.gamascape.entity.LandListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LandListingRepository extends JpaRepository<LandListing, Long> {
    List<LandListing> findByActiveTrue();
}