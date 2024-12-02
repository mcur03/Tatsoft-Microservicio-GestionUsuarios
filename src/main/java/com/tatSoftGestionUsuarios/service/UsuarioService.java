package com.tatSoftGestionUsuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tatSoftGestionUsuarios.dto.UsuarioDTO;
import com.tatSoftGestionUsuarios.model.Usuario;
import com.tatSoftGestionUsuarios.repository.UsuarioRepository;
import com.tatSoftGestionUsuarios.utils.UsuarioRespuestaDTO;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	// Metodo para crear un usuario
    public Usuario crearUsuario(UsuarioDTO usuarioDto) {
    	
        // Verificar si la cédula ya está registrada
        if (usuarioRepository.findByCedula(usuarioDto.getCedula()).isPresent()) {
            throw new IllegalArgumentException("La cédula ya está registrada.");
        }

        // Verificar si el correo ya está registrado
        if (usuarioRepository.findByCorreo(usuarioDto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
    	
        Usuario usuario = new Usuario();
        usuario.setCedula(usuarioDto.getCedula());
        usuario.setNombreCompleto(usuarioDto.getNombreCompleto());
        usuario.setCelular(usuarioDto.getCelular());
        usuario.setCorreo(usuarioDto.getCorreo());
        usuario.setContraseña(passwordEncoder.encode(usuarioDto.getContraseña()));
        usuario.setRol(Usuario.Rol.valueOf(usuarioDto.getRol().toUpperCase()));

        return usuarioRepository.save(usuario);
    }
    
    // Metodo para listar todos los usuarios
    public List<UsuarioRespuestaDTO> obtenerTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(UsuarioRespuestaDTO::new)
                .collect(Collectors.toList());
    }
}
