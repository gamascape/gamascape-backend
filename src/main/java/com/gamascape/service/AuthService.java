package com.gamascape.service;

import com.gamascape.dto.*;
import com.gamascape.entity.User;
import com.gamascape.entity.VerificationOtp;
import com.gamascape.repository.UserRepository;
import com.gamascape.repository.VerificationOtpRepository;
import com.gamascape.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final VerificationOtpRepository otpRepo;

    @Value("${google.client-id}")
    private String googleClientId;

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");
        if (user.isBlocked())
            throw new RuntimeException("Your account has been suspended. Please contact support.");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getId());
    }

    public AuthResponse loginWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID Token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            User user = userRepo.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setRole(User.Role.CLIENT);
                newUser.setPassword(encoder.encode(UUID.randomUUID().toString())); // Set a random password
                return userRepo.save(newUser);
            });

            if (user.isBlocked()) {
                throw new RuntimeException("Your account has been suspended. Please contact support.");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return new AuthResponse(token, user.getRole().name(), user.getName(), user.getId());

        } catch (Exception e) {
            log.error("Google login failed", e);
            throw new RuntimeException("Google Login failed: " + e.getMessage());
        }
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        VerificationOtp verificationOtp = otpRepo.findByEmailAndType(req.getEmail(), "REGISTRATION")
                .orElseThrow(() -> new RuntimeException("No pending registration found for this email"));

        if (verificationOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired");
        }

        if (!verificationOtp.getOtp().equals(req.getOtp())) {
            Integer attempts = verificationOtp.getAttempts();
            if (attempts == null) attempts = 0;
            verificationOtp.setAttempts(attempts + 1);
            if (verificationOtp.getAttempts() >= 5) {
                otpRepo.delete(verificationOtp);
                throw new RuntimeException("Too many failed attempts. Try requesting a new OTP.");
            }
            otpRepo.save(verificationOtp);
            throw new RuntimeException("Invalid OTP code");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setMobileNumber(req.getMobileNumber());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(User.Role.CLIENT); 
        
        userRepo.save(user);
        otpRepo.delete(verificationOtp);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getId());
    }

    private final java.util.concurrent.ConcurrentHashMap<String, Long> otpLimitMap = new java.util.concurrent.ConcurrentHashMap<>();

    private void checkRateLimit(String id) {
        long current = System.currentTimeMillis();
        Long lastRequest = otpLimitMap.get(id);
        if (lastRequest != null && (current - lastRequest) < 60000) {
            throw new RuntimeException("Too many requests. Please wait 60 seconds before requesting another OTP.");
        }
        otpLimitMap.put(id, current);
    }

    public void generateAndSendRegistrationOtp(String email) {
        checkRateLimit("REG_" + email);
        log.info("Requesting registration OTP for email: {}", email);
        
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("An account with this email already exists.");
        }

        String otp = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        
        try {
            log.info("Cleaning up old registration OTPs for {}", email);
            otpRepo.deleteByEmailAndType(email, "REGISTRATION");
            
            log.info("Saving new registration OTP for {}", email);
            VerificationOtp verificationOtp = new VerificationOtp();
            verificationOtp.setEmail(email);
            verificationOtp.setOtp(otp);
            verificationOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
            verificationOtp.setType("REGISTRATION");
            otpRepo.save(verificationOtp);

            log.info("Attempting to send OTP email to {}", email);
            emailService.sendRegistrationOtpEmail(email, otp);
            log.info("OTP successfully sent to {}", email);
            
        } catch (Exception e) {
            log.error("FAILED to process OTP for {}: {}", email, e.getMessage());
            throw new RuntimeException("Service Error: " + e.getMessage());
        }
    }

    public void generateAndSendOtp(ForgotPasswordRequest req) {
        checkRateLimit("FWD_" + req.getEmail());
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No user found with this email."));
        
        String otp = String.format("%06d", new java.security.SecureRandom().nextInt(999999));
        user.setResetOtp(otp);
        user.setResetOtpAttempts(0);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepo.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No user found with this email."));

        if (user.getResetOtp() == null) {
            throw new RuntimeException("No active reset request found.");
        }

        if (user.getResetOtpExpiry() != null && LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            throw new RuntimeException("OTP code has expired.");
        }

        if (!user.getResetOtp().equals(req.getOtp())) {
            Integer attempts = user.getResetOtpAttempts();
            if (attempts == null) attempts = 0;
            user.setResetOtpAttempts(attempts + 1);
            if (user.getResetOtpAttempts() >= 5) {
                user.setResetOtp(null);
                user.setResetOtpExpiry(null);
                user.setResetOtpAttempts(0);
                userRepo.save(user);
                throw new RuntimeException("Too many failed attempts. Password reset cancelled.");
            }
            userRepo.save(user);
            throw new RuntimeException("Invalid OTP code.");
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepo.save(user);
    }
}