package com.tatSoftGestionUsuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tatSoftGestionUsuarios.dto.UsuarioDTO;
import com.tatSoftGestionUsuarios.model.Usuario;
import com.tatSoftGestionUsuarios.model.Usuario.Rol;
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
    
    // Metodo para buscar usuario por id
    public UsuarioRespuestaDTO obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
        return new UsuarioRespuestaDTO(usuario);
    }
    
    //Metodo para actualiza un usuario
    public UsuarioRespuestaDTO actualizarUsuario(Integer id, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

        // Actualizar los campos permitidos
        usuarioExistente.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuarioExistente.setCelular(usuarioDTO.getCelular());
        usuarioExistente.setCorreo(usuarioDTO.getCorreo());
        usuarioExistente.setContraseña(usuarioDTO.getContraseña());
        usuarioExistente.setRol(Rol.valueOf(usuarioDTO.getRol().toUpperCase()));

        // Guardar cambios en la base de datos
        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);

        // Devolver los datos en forma de DTO
        return new UsuarioRespuestaDTO(usuarioActualizado);
    }
    
    // Método para eliminar un usuario
    public void eliminarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

        usuarioRepository.delete(usuario);
    }
    
	public UsuarioRespuestaDTO findByCedula(String cedula) {
		Usuario usuario = usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario con cedula " + cedula + " no encontrado"));
        return new UsuarioRespuestaDTO(usuario);
	}
}
