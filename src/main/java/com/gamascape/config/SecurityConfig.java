package com.gamascape.config;
import com.gamascape.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${allowed.origins:http://localhost:4200,http://localhost:4205,https://gamascape.com,https://www.gamascape.com}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(corsSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Public ──────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/ws/**", "/ws/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/land/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/projects").permitAll()      
                .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()   
                .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/view/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/enquiry").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/enquiry").hasAnyRole("TEAM", "ADMIN")
                
                // ── Testimonials ────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/testimonials/active").permitAll()
                .requestMatchers("/api/testimonials/**").hasAnyRole("TEAM", "ADMIN")

                // ── Authenticated ────────────────────────────────
                .requestMatchers("/api/portal/**").authenticated()

                // ── Role based ───────────────────────────────────
                .requestMatchers("/api/team/**").hasAnyRole("TEAM", "ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/files/upload/**").hasAnyRole("TEAM", "ADMIN")

                // ── Must be last ─────────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(allowedOrigins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}