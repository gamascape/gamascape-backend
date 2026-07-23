package com.gamascape.service;

import jakarta.persistence.EntityNotFoundException;


import com.gamascape.dto.EnquiryDto;
import com.gamascape.entity.Enquiry;
import com.gamascape.repository.EnquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnquiryService {

    private final EnquiryRepository repo;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public Enquiry submit(EnquiryDto dto) {
        Enquiry e = new Enquiry();
        e.setFullName(dto.getFullName());
        e.setEmail(dto.getEmail());
        e.setPhone(dto.getPhone());
        e.setInterest(dto.getInterest());
        e.setMessage(dto.getMessage());
        Enquiry saved = repo.save(e);
        messagingTemplate.convertAndSend("/topic/enquiries", saved);
        return saved;
    }

    public List<Enquiry> getAll() { return repo.findAll(); }

    public Enquiry markAsSeen(Long id) {
        Enquiry e = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Enquiry not found"));
        e.setStatus("SEEN");
        return repo.save(e);
    }

    public void deleteEnquiry(Long id) {
        repo.deleteById(id);
    }
}