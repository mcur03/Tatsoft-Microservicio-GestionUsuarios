package com.tatSoftGestionUsuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
	
	@Email
    private String correo;
	@NotBlank
    private String nuevaContraseña;
}
