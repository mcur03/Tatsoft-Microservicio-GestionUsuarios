package com.tatSoftGestionUsuarios.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	// Endpoint para crear un usuario
	@PostMapping
	public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDto) {
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
    }
	
	// Endpoint para obtener todo los usuarios
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<UsuarioRespuestaDTO> usuarios = usuarioService.obtenerTodos();
            if (usuarios.isEmpty()) {
                return ResponseEntity.ok(Collections.singletonMap("mensaje", "No hay usuarios registrados."));
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener los usuarios."); // Se manejará en GlobalExceptionHandler
        }
    }
    
 // Endpoint para buscar usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException ex) {
            // Lanzar la excepción para que sea manejada por el GlobalExceptionHandler
            throw new RuntimeException(ex.getMessage());
        }
    }
    
 // Endpoint para actualizar un usuario
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario con ID " + id + " eliminado exitosamente"));
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
