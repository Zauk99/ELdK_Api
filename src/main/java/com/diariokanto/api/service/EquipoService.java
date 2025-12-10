package com.diariokanto.api.service;

import com.diariokanto.api.dtos.EquipoDTO; // Asegúrate de tener este DTO en la API también o usa Object si vas con prisa
import com.diariokanto.api.entity.*;
import com.diariokanto.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired private EquipoRepository equipoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public List<Equipo> obtenerTodos() {
        return equipoRepository.findAllByOrderByFechaCreacionDesc();
    }

    public void crearEquipo(EquipoDTO dto, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Equipo equipo = new Equipo();
        equipo.setNombre(dto.getNombre());
        equipo.setDescripcion(dto.getDescripcion());
        equipo.setFechaCreacion(LocalDateTime.now());
        equipo.setUsuario(usuario);

        // Convertir miembros DTO a Entidad
        if (dto.getMiembros() != null) {
            for (var mDto : dto.getMiembros()) {
                MiembroEquipo miembro = new MiembroEquipo();
                miembro.setNombrePokemon(mDto.getNombrePokemon());
                miembro.setMote(mDto.getMote());
                // ... aquí setearías movimientos, objetos, etc.
                equipo.addMiembro(miembro);
            }
        }
        equipoRepository.save(equipo);
    }
}