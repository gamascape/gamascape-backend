package com.gamascape.repository;

import com.gamascape.entity.VerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface VerificationOtpRepository extends JpaRepository<VerificationOtp, Long> {
    
    Optional<VerificationOtp> findByEmailAndOtpAndType(String email, String otp, String type);
    
    Optional<VerificationOtp> findByEmailAndType(String email, String type);

    
    @Modifying
    @Transactional
    void deleteByEmailAndType(String email, String type);
}
