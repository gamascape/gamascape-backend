package com.gamascape.controller;

import com.gamascape.dto.*;
import com.gamascape.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.samesite}")
    private String cookieSameSite;

    private void attachCookie(HttpServletResponse res, String token) {
        ResponseCookie cookie = ResponseCookie.from("gama_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(86400)
                .sameSite(cookieSameSite)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req, HttpServletResponse res) {
        AuthResponse auth = authService.login(req);
        attachCookie(res, auth.getToken());
        auth.setToken(null); // Clear from body
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleAuthRequest req, HttpServletResponse res) {
        AuthResponse auth = authService.loginWithGoogle(req.getIdToken());
        attachCookie(res, auth.getToken());
        auth.setToken(null); // Clear from body
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @jakarta.validation.Valid RegisterRequest req, HttpServletResponse res) {
        AuthResponse auth = authService.register(req);
        attachCookie(res, auth.getToken());
        auth.setToken(null); // Clear from body
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("gama_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite(cookieSameSite)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-registration-otp")
    public ResponseEntity<Map<String, String>> requestRegistrationOtp(@RequestParam String email) {
        authService.generateAndSendRegistrationOtp(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to your email.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @jakarta.validation.Valid ForgotPasswordRequest req) {
        authService.generateAndSendOtp(req);
        Map<String, String> response = new HashMap<>();
        response.put("message", "If an account exists, an OTP has been sent.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @jakarta.validation.Valid ResetPasswordRequest req) {
        authService.resetPassword(req);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password successfully reset.");
        return ResponseEntity.ok(response);
    }
}