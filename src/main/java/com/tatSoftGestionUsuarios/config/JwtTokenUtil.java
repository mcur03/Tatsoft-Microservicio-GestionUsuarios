package com.tatSoftGestionUsuarios.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

	private static String secretKey; // Clave secreta para firmar tokens
    private static final long EXPIRATION_TIME = 3600000; // 1 hora (en milisegundos)

    @Value("${jwt.secret.key}")
    public void setSecretKey(String key) {
        JwtTokenUtil.secretKey = key;
    }
    
    
    public static String generateToken(String userId, String role) {
        return JWT.create()
                .withSubject(userId) // Identificador del usuario
                .withClaim("role", role) // Información adicional (claims)
                .withIssuedAt(new Date()) // Fecha de emisión
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiración
                .sign(Algorithm.HMAC256(secretKey)); // Firma con HMAC256 y clave secreta

    }
}
