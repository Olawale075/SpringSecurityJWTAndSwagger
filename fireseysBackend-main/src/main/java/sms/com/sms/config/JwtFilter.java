package sms.com.sms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import sms.com.sms.service.UserService;
import sms.com.sms.service.UserServiceImpl;

@Component
@RequiredArgsConstructor  // ✅ This ensures dependencies are injected automatically
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userDetailsService;
 // ✅ Injected via constructor
    private final JwtUtil jwtUtil;  // ✅ Injected via constructor

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;  // ✅ Skip if there's no valid token
        }

        String jwt = authorizationHeader.substring(7);
        String phoneNumber = jwtUtil.extractUsername(jwt);

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {  // ✅ Validate token
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
