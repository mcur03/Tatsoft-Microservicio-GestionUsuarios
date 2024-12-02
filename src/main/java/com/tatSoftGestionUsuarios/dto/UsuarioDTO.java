package com.tatSoftGestionUsuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioDTO {
	
	@NotBlank(message = "La cédula es obligatoria")
    @Size(max = 15, message = "La cédula no puede tener más de 15 caracteres")
    private String cedula;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120, message = "El nombre completo no puede tener más de 120 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 15, message = "El celular no puede tener más de 15 caracteres")
    private String celular;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

}
