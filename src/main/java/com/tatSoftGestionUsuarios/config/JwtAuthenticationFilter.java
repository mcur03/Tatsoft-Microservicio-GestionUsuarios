package com.tatSoftGestionUsuarios.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
            	 DecodedJWT decodedJWT = JWT.decode(token); 
                System.out.println("Decoded JWT: " + decodedJWT.toString());

                String cedula = decodedJWT.getClaim("cedula").asString(); // Usualmente el userId está en el 'subject'
                System.out.println("cedula: " + cedula);
                
                String role = decodedJWT.getClaim("role").asString();
                System.out.println("Role: " + role);
                
                if (role != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(cedula, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))); 
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (JWTVerificationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
