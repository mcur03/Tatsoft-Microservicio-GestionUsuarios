package com.tatSoftGestionUsuarios.utils;

import com.tatSoftGestionUsuarios.model.Usuario;

import lombok.Data;

@Data
public class UsuarioRespuestaDTO {
    private String cedula;
    private String nombreCompleto;
    private String celular;
    private String correo;
    private String rol;
    private String contraseña;

    public UsuarioRespuestaDTO(Usuario usuario) {
        this.cedula = usuario.getCedula();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.celular = usuario.getCelular();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol().name();
        this.contraseña = usuario.getContraseña();
    }
}
