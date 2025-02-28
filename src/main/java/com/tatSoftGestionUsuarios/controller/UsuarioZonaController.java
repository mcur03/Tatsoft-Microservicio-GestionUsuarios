package com.tatSoftGestionUsuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tatSoftGestionUsuarios.config.JwtService;
import com.tatSoftGestionUsuarios.dto.AsignarZonasRequest;
import com.tatSoftGestionUsuarios.service.UsuarioService;
import com.tatSoftGestionUsuarios.service.UsuarioZonaService;
import com.tatSoftGestionUsuarios.utils.UsuarioRespuestaDTO;

import java.util.Map;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioZonaController {

    @Autowired
    private UsuarioZonaService usuarioZonaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private JwtService jwtService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/asignar-zonas/{id_usuario}")
    public ResponseEntity<?> asignarZonasAUsuario(@PathVariable Integer id_usuario, 
                                                  @RequestBody AsignarZonasRequest request) {
        try {
            if (request.getZonas() == null || request.getZonas().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debes enviar un array de zonas."));
            }
            
            Map<String, Object> resultado = usuarioZonaService.asignarZonas(id_usuario, request.getZonas());
            return ResponseEntity.ok(Map.of(
                "message", "Zonas asignadas correctamente",
                "resultado", resultado
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error asignando zonas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/zonas/{id_usuario}")
    public ResponseEntity<?> obtenerZonasUsuarioAdmin(@PathVariable Integer id_usuario) {
        try {
            Map<String, Object> resultado = usuarioZonaService.obtenerZonasDeUsuario(id_usuario, null, true);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error obteniendo zonas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }

    // Endpoint para colaboradores - solo pueden ver sus propias zonas
    @GetMapping("/mis-zonas")
    public ResponseEntity<?> obtenerMisZonas(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header
            String token = authHeader.replace("Bearer ", "");
            
            // Extraer el ID del usuario del token
            Integer userId = jwtService.extractUserId(token).intValue();
            
            // Obtener el usuario para determinar su rol
            UsuarioRespuestaDTO usuario = usuarioService.obtenerPorId(userId);
            boolean isAdmin = "ADMINISTRADOR".equals(usuario.getRol());
            
            Map<String, Object> resultado = usuarioZonaService.obtenerZonasDeUsuario(userId, userId, isAdmin);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("Error obteniendo zonas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }

    // Endpoint  ver clientes de las zonas asignadas
    @GetMapping("/getclientes-zonas/{id_zona}")
    public ResponseEntity<?> obtenerClientesZonaColaborador(
            @PathVariable Integer id_zona,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header
            String token = authHeader.replace("Bearer ", "");
            
            // Extraer el ID del usuario del token
            Integer userId = jwtService.extractUserId(token).intValue();
            
            // Obtener el usuario para determinar su rol
            UsuarioRespuestaDTO usuario = usuarioService.obtenerPorId(userId);
            boolean isAdmin = "ADMINISTRADOR".equals(usuario.getRol());
            
            Map<String, Object> resultado = usuarioZonaService.obtenerClientesPorZona(id_zona, userId, isAdmin);
            return ResponseEntity.ok(resultado);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error obteniendo clientes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    
    // Endpoint para que un administrador elimine una zona específica de un usuario
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/eliminar-zona/{id_usuario}/{id_zona}")
    public ResponseEntity<?> eliminarZonaDeUsuario(
            @PathVariable Integer id_usuario,
            @PathVariable Integer id_zona) {
        try {
            Map<String, Object> resultado = usuarioZonaService.eliminarZonaDeUsuario(id_usuario, id_zona);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error eliminando zona: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }

  
     //Endpoint para que un administrador elimine todas las zonas asignadas a un usuario
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/eliminar-zonas/{id_usuario}")
    public ResponseEntity<?> eliminarTodasZonasDeUsuario(@PathVariable Integer id_usuario) {
        try {
            Map<String, Object> resultado = usuarioZonaService.eliminarTodasZonasDeUsuario(id_usuario);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error eliminando zonas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
}