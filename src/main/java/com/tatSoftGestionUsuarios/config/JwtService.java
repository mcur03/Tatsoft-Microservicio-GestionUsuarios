package com.tatSoftGestionUsuarios.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	@Value("${jwt.secret.key}")
    private String jwtSecret;
	
	public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
    }

	public Integer extractUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        // Intenta obtener como número primero
        Integer id = jwt.getClaim("id_usuario").asInt();
        System.out.println("ID_USUARIO: " + id);
        if (id != null) {
            return id;
        }
        // Si no funciona, intenta como string
        String idStr = jwt.getClaim("id_usuario").asString();
        if (idStr != null) {
            return Integer.parseInt(idStr);
        }
        throw new RuntimeException("No se pudo extraer el ID del usuario del token JWT");
    }
    
    public String extractCedula(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("cedula").asString();
    }
    
}
