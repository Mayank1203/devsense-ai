package com.devsense.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = this filter runs exactly once per HTTP request

    private final JwtTokenProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        // Step 1: Extract the token from the Authorization header
        String header = request.getHeader("Authorization");
        // Client sends: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);  // remove 'Bearer ' prefix

            // Step 2: Validate the token
            if (jwtProvider.isValid(token)) {
                String email = jwtProvider.extractEmail(token);

                // Step 3: Load user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 4: Tell Spring Security this user is authenticated
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,               // credentials (null for JWT — already validated)
                                userDetails.getAuthorities()
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                // After this line, @PreAuthorize and @AuthenticationPrincipal work
            }
        }

        // Step 5: Always continue the filter chain
        // Even unauthenticated requests pass through — Spring Security decides
        // whether to allow or deny based on the SecurityConfig rules
        chain.doFilter(request, response);
    }
}
