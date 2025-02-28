package com.tatSoftGestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.tatSoftGestionUsuarios.model.UsuarioZona;
import com.tatSoftGestionUsuarios.repository.UsuarioRepository;
import com.tatSoftGestionUsuarios.repository.UsuarioZonaRepository;

import io.swagger.v3.oas.models.PathItem.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioZonaService {
    
    @Autowired
    private UsuarioZonaRepository usuarioZonaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Transactional
    public Map<String, Object> asignarZonas(Integer idUsuario, List<Integer> zonas) {
        // Validar si el usuario existe
        if (!usuarioRepository.findById(idUsuario).isPresent()) {
            throw new IllegalArgumentException("Usuario con ID " + idUsuario + " no encontrado.");
        }
        
        // Validar que las zonas existan en el microservicio de Clientes-Zonas
        List<Map<String, Object>> zonasExistentes = new ArrayList<>();
        
        for (Integer idZona : zonas) {
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(
                    "https://backendareasandclients-apgba5dxbrbwb2ex.eastus2-01.azurewebsites.net/get_dataArea/{id}", 
                    Map.class, 
                    idZona
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    zonasExistentes.add(response.getBody());
                }
            } catch (Exception e) {
                // Si hay error, la zona no existe o no se puede conectar al servicio
                System.err.println("Error al verificar zona " + idZona + ": " + e.getMessage());
            }
        }
        
        if (zonasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Ninguna de las zonas especificadas existe.");
        }
        
        // Obtener solo los IDs de las zonas válidas
        List<Integer> zonasIds = new ArrayList<>();
        for (Map<String, Object> zona : zonasExistentes) {
            zonasIds.add((Integer) zona.get("id_zona_de_trabajo"));
        }
        
     // Asignar nuevas zonas
        int asignadas = 0;
        for (Integer idZona : zonasIds) {
            //usuarioZonaRepository.insertarZonaUsuario(idUsuario, idZona);
        	UsuarioZona usuarioZona = new UsuarioZona(idUsuario, idZona);
            usuarioZonaRepository.save(usuarioZona);
            asignadas++;
        }
        
        // Construir respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("zonasAsignadas", asignadas);
        resultado.put("zonasValidas", zonasIds);
        
        return resultado;
    }
    
    
    public Map<String, Object> obtenerZonasDeUsuario(Integer idUsuario, Integer usuarioSolicitanteId, boolean isAdmin) {
        // Verificar si el usuario existe
        if (!usuarioRepository.findById(idUsuario).isPresent()) {
            throw new IllegalArgumentException("Usuario con ID " + idUsuario + " no encontrado.");
        }
        
        // Verificar permisos: solo admin puede ver zonas de otros usuarios
        if (!isAdmin && !idUsuario.equals(usuarioSolicitanteId)) {
            throw new AccessDeniedException("No tienes permisos para ver las zonas de este usuario.");
        }
        
        // Obtener zonas del usuario
        List<Integer> zonasIds = usuarioZonaRepository.findZonasByUsuario(idUsuario);
        
        // Obtener detalles de las zonas si es posible
        List<Map<String, Object>> detallesZonas = new ArrayList<>();
        for (Integer idZona : zonasIds) {
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(
                    "https://backendareasandclients-apgba5dxbrbwb2ex.eastus2-01.azurewebsites.net/get_dataArea/{id}",
                    Map.class,
                    idZona
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    detallesZonas.add(response.getBody());
                } else {
                    // Si no se pueden obtener los detalles, al menos incluir el ID
                    Map<String, Object> zonaBasica = new HashMap<>();
                    zonaBasica.put("id_zona_de_trabajo", idZona);
                    zonaBasica.put("nota", "No se pudieron obtener detalles completos");
                    detallesZonas.add(zonaBasica);
                }
            } catch (Exception e) {
                // Si hay error al conectar con el microservicio, incluir al menos el ID
                Map<String, Object> zonaBasica = new HashMap<>();
                zonaBasica.put("id_zona_de_trabajo", idZona);
                zonaBasica.put("nota", "No se pudieron obtener detalles");
                detallesZonas.add(zonaBasica);
            }
        }
        
        // Construir respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("idUsuario", idUsuario);
        resultado.put("cantidadZonas", zonasIds.size());
        resultado.put("zonas", detallesZonas);
        
        return resultado;
    }
    
    
    public Map<String, Object> obtenerClientesPorZona(Integer idZona, Integer usuarioSolicitanteId, boolean isAdmin) {
        // Verificar permisos: si no es admin, debe tener la zona asignada
    	if (!isAdmin) {
    	    Integer tieneAcceso = usuarioZonaRepository.usuarioTieneZonaAsignada(usuarioSolicitanteId, idZona);
    	    if (tieneAcceso == 0) {  // Si es 0, significa que no tiene acceso
    	        throw new AccessDeniedException("No tienes acceso a esta zona.");
    	    }
    	}
        
        // Consultar al microservicio de clientes-zonas para obtener los clientes de la zona
        try {
            // Primero verificamos que la zona existe
            ResponseEntity<Map> zonaResponse = restTemplate.getForEntity(
                "https://backendareasandclients-apgba5dxbrbwb2ex.eastus2-01.azurewebsites.net/get_dataArea/{id_area}",
                Map.class,
                idZona
            );
            
            if (!zonaResponse.getStatusCode().is2xxSuccessful() || zonaResponse.getBody() == null) {
                throw new IllegalArgumentException("La zona especificada no existe o no hay clientes asignados");
            }
            
            // Ahora obtenemos los clientes de esa zona
            ResponseEntity<List<Map<String, Object>>> clientesResponse = restTemplate.exchange(
            	    "https://backendareasandclients-apgba5dxbrbwb2ex.eastus2-01.azurewebsites.net/get_clientArea/{idZona}",
            	    org.springframework.http.HttpMethod.GET, 
            	    null,
            	    new ParameterizedTypeReference<List<Map<String, Object>>>() {},
            	    idZona
            	);
            
            if (clientesResponse.getStatusCode().is2xxSuccessful() && clientesResponse.getBody() != null) {
                List<Map<String, Object>> clientes = clientesResponse.getBody();
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("idZona", idZona);
                resultado.put("nombreZona", zonaResponse.getBody().get("nombre_zona_trabajo"));
                resultado.put("clientes", clientes);
                resultado.put("totalClientes", clientes.size());
                return resultado;
            } else {
                // Si no hay clientes, devolvemos una lista vacía
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("idZona", idZona);
                resultado.put("nombreZona", zonaResponse.getBody().get("nombre_zona_trabajo"));
                resultado.put("clientes", new ArrayList<>());
                resultado.put("totalClientes", 0);
                return resultado;
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("La zona especificada no existe.");
        } catch (Exception e) {
            System.err.println("Error al consultar clientes de la zona " + idZona + ": " + e.getMessage());
            throw new RuntimeException("Error al consultar el servicio de zonas-clientes: " + e.getMessage());
        }
    }
    
    
    // Elimina una zona específica asignada a un usuario
     
    @Transactional
    public Map<String, Object> eliminarZonaDeUsuario(Integer idUsuario, Integer idZona) {
        // Verificar si el usuario existe
        if (!usuarioRepository.findById(idUsuario).isPresent()) {
            throw new IllegalArgumentException("Usuario con ID " + idUsuario + " no encontrado.");
        }
        
        // Verificar si el usuario tiene esa zona asignada
        Integer tieneZona = usuarioZonaRepository.usuarioTieneZonaAsignada(idUsuario, idZona);
        if (tieneZona == 0) {
            throw new IllegalArgumentException("El usuario no tiene asignada la zona especificada.");
        }
        
        // Eliminar la asignación
        usuarioZonaRepository.eliminarZonaDeUsuario(idUsuario, idZona);
        
        // Obtener las zonas que quedan asignadas
        List<Integer> zonasRestantes = usuarioZonaRepository.findZonasByUsuario(idUsuario);
        
        // Construir respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("mensaje", "Zona eliminada correctamente del usuario.");
        resultado.put("idUsuario", idUsuario);
        resultado.put("idZonaEliminada", idZona);
        resultado.put("zonasRestantes", zonasRestantes);
        resultado.put("cantidadZonasRestantes", zonasRestantes.size());
        
        return resultado;
    }

    
     //Elimina todas las zonas asignadas a un usuario
    @Transactional
    public Map<String, Object> eliminarTodasZonasDeUsuario(Integer idUsuario) {
        // Verificar si el usuario existe
        if (!usuarioRepository.findById(idUsuario).isPresent()) {
            throw new IllegalArgumentException("Usuario con ID " + idUsuario + " no encontrado.");
        }
        
        // Obtener las zonas antes de eliminarlas (para incluirlas en la respuesta)
        List<Integer> zonasEliminadas = usuarioZonaRepository.findZonasByUsuario(idUsuario);
        
        // Eliminar todas las asignaciones
        usuarioZonaRepository.eliminarZonasUsuario(idUsuario);
        
        // Construir respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("mensaje", "Todas las zonas han sido eliminadas del usuario.");
        resultado.put("idUsuario", idUsuario);
        resultado.put("zonasEliminadas", zonasEliminadas);
        resultado.put("cantidadZonasEliminadas", zonasEliminadas.size());
        
        return resultado;
    }
}