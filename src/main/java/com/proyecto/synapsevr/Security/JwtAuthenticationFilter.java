package com.proyecto.synapsevr.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        System.out.println("=====================================");
        System.out.println("🚀 NUEVA PETICIÓN: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("🔑 Header Authorization: " + authHeader);

        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("🔑 JWT encontrado: SÍ");

            try {
                username = jwtConfig.extractUsername(jwt);
                System.out.println("🔑 Username del token: " + username);
            } catch (Exception e) {
                System.out.println("❌ ERROR extrayendo username: " + e.getMessage());
            }
        } else {
            System.out.println("❌ NO hay header Authorization válido");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔄 Validando token...");

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtConfig.isTokenValid(jwt, userDetails)) {
                System.out.println("✅ TOKEN VÁLIDO - Configurando autenticación");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("❌ TOKEN INVÁLIDO");
            }
        }

        System.out.println("=====================================");
        filterChain.doFilter(request, response);
    }
}