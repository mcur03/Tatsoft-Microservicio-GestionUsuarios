package com.tatSoftGestionUsuarios.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import com.tatSoftGestionUsuarios.model.UsuarioZona;

import java.util.List;

@Repository
public interface UsuarioZonaRepository extends JpaRepository<UsuarioZona, Integer> {
	
    @Modifying
    @Query(value = "DELETE FROM usuario_zona WHERE id_usuario = :idUsuario", nativeQuery = true)
    void eliminarZonasUsuario(@Param("idUsuario") Integer idUsuario);
    
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "INSERT IGNORE INTO usuario_zona (id_usuario, id_zona_de_trabajo) VALUES (:idUsuario, :idZona)", nativeQuery = true)
	void insertarZonaUsuario(@Param("idUsuario") Integer idUsuario, @Param("idZona") Integer idZona);
	
	 @Query(value = "SELECT id_zona_de_trabajo FROM usuario_zona WHERE id_usuario = :idUsuario", nativeQuery = true)
	    List<Integer> findZonasByUsuario(@Param("idUsuario") Integer idUsuario);
	 
	 @Query(value = "SELECT COUNT(*) FROM usuario_zona WHERE id_usuario = :idUsuario AND id_zona_de_trabajo = :idZona", nativeQuery = true)
	 Integer usuarioTieneZonaAsignada(@Param("idUsuario") Integer idUsuario, @Param("idZona") Integer idZona);
	 
	 @Modifying
	 @Transactional
	 @Query(value = "DELETE FROM usuario_zona WHERE id_usuario = :idUsuario AND id_zona_de_trabajo = :idZona", nativeQuery = true)
	 void eliminarZonaDeUsuario(@Param("idUsuario") Integer idUsuario, @Param("idZona") Integer idZona);

	
}