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

        // Permitir endpoints públicos sin JWT
        String path = request.getRequestURI();
        System.out.println("🔍 [JWT FILTER] Checking path: " + path);
        
        if (path.contains("/api/auth/register") || 
            path.contains("/api/auth/login") ||
            path.contains("/api/auth/forgot-password") ||
            path.contains("/api/auth/reset-password") ||
            path.contains("/api/auth/verify-reset-code") ||
            path.contains("/api/auth/test") ||
            path.contains("/api/auth/check-email")) {
            System.out.println("✅ [JWT FILTER] Public endpoint, skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }

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

                    // Verificar validez del token con más detalle
                    boolean isValid = jwtConfig.isTokenValid(jwt, userDetails);
                    System.out.println("🔍 ¿Token válido? " + isValid);
                    
                    if (isValid) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("✅ Autenticación exitosa para: " + username);
                    } else {
                        System.out.println("❌ Token inválido para usuario: " + username);
                        // Verificar por qué es inválido
                        try {
                            java.util.Date expiration = jwtConfig.extractExpiration(jwt);
                            System.out.println("🔍 Token expira en: " + expiration);
                            System.out.println("🔍 Fecha actual: " + new java.util.Date());
                            System.out.println("🔍 ¿Expirado? " + expiration.before(new java.util.Date()));
                        } catch (Exception ex) {
                            System.out.println("❌ Error verificando expiración: " + ex.getMessage());
                        }
                    }
                } else {
                    if (username == null) {
                        System.out.println("❌ Username es nulo");
                    } else {
                        System.out.println("❌ Usuario ya autenticado: " + SecurityContextHolder.getContext().getAuthentication());
                    }
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