package com.devsense.security;

import com.devsense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // Lombok: generates constructor for all final fields
public class UserDetailsServiceImpl implements UserDetailsService {
    // Spring Security needs this interface to load users during authentication

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security calls this with the 'username' — in our case it's the email
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())  // the BCrypt hash
                        .authorities("ROLE_" + user.getPlan().name())
                        // ROLE_FREE | ROLE_PRO | ROLE_TEAM
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}

