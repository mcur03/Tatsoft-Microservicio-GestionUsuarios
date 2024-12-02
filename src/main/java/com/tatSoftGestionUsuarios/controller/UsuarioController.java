package com.tatSoftGestionUsuarios.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tatSoftGestionUsuarios.dto.UsuarioDTO;
import com.tatSoftGestionUsuarios.model.Usuario;
import com.tatSoftGestionUsuarios.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
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

}
