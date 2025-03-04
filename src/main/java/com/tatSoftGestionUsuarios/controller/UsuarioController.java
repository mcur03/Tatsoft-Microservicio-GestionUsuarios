package com.tatSoftGestionUsuarios.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tatSoftGestionUsuarios.config.JwtService;
import com.tatSoftGestionUsuarios.dto.UpdatePasswordRequest;
import com.tatSoftGestionUsuarios.dto.UsuarioDTO;
import com.tatSoftGestionUsuarios.model.Usuario;
import com.tatSoftGestionUsuarios.service.UsuarioService;
import com.tatSoftGestionUsuarios.utils.UsuarioRespuestaDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
    private JwtService jwtService;
	
	// Endpoint para crear un usuario
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PostMapping
	public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDto) {
        try {
        	Usuario usuario = usuarioService.crearUsuario(usuarioDto);

            // Crear un mensaje de éxito con los datos del usuario
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario creado exitosamente");
            response.put("usuario", Map.of(
                    "cedula", usuario.getCedula(),
                    "nombreCompleto", usuario.getNombreCompleto(),
                    "celular", usuario.getCelular(),
                    "correo", usuario.getCorreo(),
                    "rol", usuario.getRol()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
    }
	
	// Endpoint para obtener todo los usuarios
	@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<UsuarioRespuestaDTO> usuarios = usuarioService.obtenerTodos();
            if (usuarios.isEmpty()) {
                return ResponseEntity.ok(Collections.singletonMap("mensaje", "No hay usuarios registrados."));
            }
            return ResponseEntity.ok(usuarios);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage()); // Se manejará en GlobalExceptionHandler
        }
    }
    
 // Endpoint para buscar usuario por id
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
	@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
 // Endpoint para actualizar un usuario
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
	@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            UsuarioRespuestaDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    // Endpoint para eliminar un usuario
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
	@PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario con ID " + id + " eliminado exitosamente"));
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    // Endpoint para la peticion del microservicio autenticacion
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<?> obtenerPorCedula(@PathVariable String cedula) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.findByCedula(cedula);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
 // Endpoint para la peticion del microservicio autenticacion para restablacer contraseña
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
    @GetMapping("/email/{email}")
    public ResponseEntity<?> obtenerPorCorreo(@PathVariable String email) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.findByCorreo(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    // endpoint para restaurar la contraseña
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        try {
            UsuarioRespuestaDTO usuarioActualizado = usuarioService.restablecerContraseña(request);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    
    // endpoint para consultar el usuario por id para otro micorservicio 
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
    @GetMapping("/id_usuario/{id_usuario}")
    public ResponseEntity<?> getNombreUsuario(@PathVariable Integer id_usuario) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.obtenerPorId(id_usuario);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    // Para mirar mi perfil
	@CrossOrigin(origins = {"http://localhost:5173", "https://ambitious-sky-070d67b0f.4.azurestaticapps.net"})
    @GetMapping("/perfil")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header (eliminar "Bearer ")
            String token = authHeader.replace("Bearer ", "");
            
            // Extraer el ID del usuario del token
            Integer userId = jwtService.extractUserId(token).intValue();
            
            // Consultar el usuario en la base de datos
            UsuarioRespuestaDTO user = usuarioService.obtenerPorId(userId);
            
            return ResponseEntity.ok(user);
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
        	System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    

}
