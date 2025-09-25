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

        String authHeader = request.getHeader("Authorization");

        // DEBUG - Agregar logs temporales
        System.out.println("🔍 REQUEST URL: " + request.getRequestURL().toString());
        System.out.println("🔍 Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            System.out.println("🔍 JWT Token (primeros 50 chars): " + jwt.substring(0, Math.min(50, jwt.length())));

            try {
                String username = jwtConfig.extractUsername(jwt);
                System.out.println("🔍 Username extraído del token: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    System.out.println("🔍 UserDetails encontrado: " + userDetails.getUsername());
                    System.out.println("🔍 Authorities: " + userDetails.getAuthorities());

                    if (jwtConfig.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("🔍 Autenticación exitosa para: " + username);
                    } else {
                        System.out.println("❌ Token inválido para usuario: " + username);
                    }
                } else {
                    System.out.println("❌ Username nulo o ya autenticado");
                }
            } catch (Exception e) {
                System.out.println("❌ Error procesando token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ No hay Authorization header o no empieza con Bearer");
        }

        filterChain.doFilter(request, response);
    }
}