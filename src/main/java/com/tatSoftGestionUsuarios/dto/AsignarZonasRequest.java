package com.tatSoftGestionUsuarios.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class AsignarZonasRequest {
    
    @NotEmpty(message = "La lista de zonas no puede estar vacía")
    private List<Integer> zonas;

    public List<Integer> getZonas() {
        return zonas;
    }

    public void setZonas(List<Integer> zonas) {
        this.zonas = zonas;
    }
}