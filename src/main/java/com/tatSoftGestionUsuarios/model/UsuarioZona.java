package com.tatSoftGestionUsuarios.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "usuario_zona")
@Data
public class UsuarioZona{
    
    @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Integer id;
    
       @Column(name = "id_usuario")
       @NotNull(message = "El ID del usuario es obligatorio")
       private Integer idUsuario;
    
       @NotNull(message = "Debe de haber por lo menos una zona")
       @Column(name = "id_zona_de_trabajo")
       private Integer idZona;
    
       public UsuarioZona() {
       }
    
       public UsuarioZona(Integer idUsuario, Integer idZona) {
           this.idUsuario = idUsuario;
           this.idZona = idZona;
       }
}