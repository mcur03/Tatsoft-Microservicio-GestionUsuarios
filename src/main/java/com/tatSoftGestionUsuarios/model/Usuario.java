package com.tatSoftGestionUsuarios.model;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer idUsuario;

	    @NotBlank(message = "La cédula es obligatoria")
	    @Size(max = 15, message = "La cédula no puede tener más de 15 caracteres")
	    @Column(nullable = false, unique = true)
	    private String cedula;

	    @NotBlank(message = "El nombre completo es obligatorio")
	    @Size(max = 120, message = "El nombre completo no puede tener más de 120 caracteres")
	    private String nombreCompleto;

	    @NotBlank(message = "El celular es obligatorio")
	    @Size(max = 15, message = "El celular no puede tener más de 15 caracteres")
	    private String celular;

	    @NotBlank(message = "El correo es obligatorio")
	    @Email(message = "Debe ser un correo válido")
	    @Size(max = 255)
	    @Column(nullable = false, unique = true)
	    private String correo;

	    @NotBlank(message = "La contraseña es obligatoria")
	    private String contraseña;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Rol rol;

	    @Column(updatable = false)
	    private LocalDateTime fechaCreacion = LocalDateTime.now();

	    public enum Rol {
	        ADMINISTRADOR,
	        COLABORADOR
	    }
}
