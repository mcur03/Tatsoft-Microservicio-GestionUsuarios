package com.tatSoftGestionUsuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tatSoftGestionUsuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

	 Optional<Usuario> findByCedula(String cedula);
	 Optional<Usuario> findByCorreo(String cedula);
	
}
