package com.diariokanto.api.controller;

import com.diariokanto.api.dtos.EquipoDTO;
import com.diariokanto.api.entity.Equipo; // Usamos entidad para simplificar la lista rápida
import com.diariokanto.api.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired private EquipoService equipoService;

    // GET /api/equipos -> Devuelve todos para la comunidad
    @GetMapping
    public List<Map<String, Object>> listarTodos() {
        // Transformamos la entidad a un mapa simple para el JSON
        return equipoService.obtenerPublicos().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("nombre", e.getNombre());
            map.put("descripcion", e.getDescripcion());
            map.put("usuarioNombre", e.getUsuario().getUsername());
            map.put("publico", e.isPublico());
            // Lista simple de miembros
            List<Map<String, String>> miembros = new ArrayList<>();
            e.getMiembros().forEach(m -> {
                Map<String, String> pm = new HashMap<>();
                pm.put("nombrePokemon", m.getNombrePokemon());
                miembros.add(pm);
            });
            map.put("miembros", miembros);
            
            return map;
        }).collect(Collectors.toList());
    }

    // POST /api/equipos/crear?email=...
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody EquipoDTO equipo, @RequestParam String email) {
        try {
            equipoService.crearEquipo(equipo, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/equipos/mis-equipos (Privado: Todos los míos)
    @GetMapping("/mis-equipos")
    public List<Map<String, Object>> listarMisEquipos(@RequestParam String email) {
        // Misma lógica de mapeo que listarComunidad pero llamando a obtenerPorUsuario(email)
         return equipoService.obtenerPorUsuario(email).stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("nombre", e.getNombre());
            map.put("descripcion", e.getDescripcion());
            map.put("usuarioNombre", e.getUsuario().getUsername());
            map.put("publico", e.isPublico());
            // Lista simple de miembros
            List<Map<String, String>> miembros = new ArrayList<>();
            e.getMiembros().forEach(m -> {
                Map<String, String> pm = new HashMap<>();
                pm.put("nombrePokemon", m.getNombrePokemon());
                miembros.add(pm);
            });
            map.put("miembros", miembros);
            
            return map;
        }).collect(Collectors.toList());
    }
}