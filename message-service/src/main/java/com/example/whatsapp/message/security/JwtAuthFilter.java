package com.example.whatsapp.message.security;

import com.yourorg.common.security.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String userId = JwtUtil.validateAndGetUser(token);
                req.setAttribute("userId", userId);
            } catch (Exception ignored) {
                // Intentionally ignored – non-blocking
            }
        }

        chain.doFilter(request, response);
    }
}
