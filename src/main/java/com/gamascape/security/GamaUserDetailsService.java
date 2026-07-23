package com.gamascape.security;

import com.gamascape.entity.User;
import com.gamascape.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,                 // enabled
                true,                 // accountNonExpired
                true,                 // credentialsNonExpired
                !user.isBlocked(),    // accountNonLocked -> Blocked users cannot authenticate
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}