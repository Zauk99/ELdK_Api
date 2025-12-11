package com.diariokanto.api.service;

import com.diariokanto.api.dtos.EquipoDTO; // Asegúrate de tener este DTO en la API también o usa Object si vas con prisa
import com.diariokanto.api.dtos.MiembroEquipoDTO;
import com.diariokanto.api.entity.*;
import com.diariokanto.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Equipo> obtenerPublicos() {
        return equipoRepository.findAllByPublicoTrueOrderByFechaCreacionDesc();
    }

    // Método para el perfil (todos los del usuario)
    public List<Equipo> obtenerPorUsuario(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElse(null);
        if (u == null)
            return List.of();
        return equipoRepository.findByUsuario_Id(u.getId());
    }

    // ... imports ...
    
    public void crearEquipo(EquipoDTO dto, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Equipo equipo;

        // 1. Lógica de Actualización vs Creación
        if (dto.getId() != null) {
            equipo = equipoRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            
            if (!equipo.getUsuario().getEmail().equals(emailUsuario)) {
                 throw new RuntimeException("No tienes permiso");
            }
            equipo.getMiembros().clear(); // Borramos viejos para reescribir
        } else {
            equipo = new Equipo();
            equipo.setFechaCreacion(LocalDateTime.now());
            equipo.setUsuario(usuario);
        }

        equipo.setNombre(dto.getNombre());
        equipo.setDescripcion(dto.getDescripcion());
        equipo.setPublico(dto.isPublico());

        // 2. Guardar miembros con TODOS sus detalles
        if (dto.getMiembros() != null) {
            for (var mDto : dto.getMiembros()) {
                MiembroEquipo miembro = new MiembroEquipo();
                
                miembro.setNombrePokemon(mDto.getNombrePokemon());
                miembro.setMote(mDto.getMote());
                
                // --- ASEGÚRATE DE QUE ESTAS LÍNEAS ESTÁN ---
                miembro.setObjeto(mDto.getObjeto());         // <--- IMPORTANTE
                miembro.setHabilidad(mDto.getHabilidad());   // <--- IMPORTANTE
                miembro.setNaturaleza(mDto.getNaturaleza()); // <--- IMPORTANTE
                
                miembro.setMovimiento1(mDto.getMovimiento1()); // <--- IMPORTANTE
                miembro.setMovimiento2(mDto.getMovimiento2());
                miembro.setMovimiento3(mDto.getMovimiento3());
                miembro.setMovimiento4(mDto.getMovimiento4());
                // -------------------------------------------

                equipo.addMiembro(miembro);
            }
        }
        equipoRepository.save(equipo);
    }

    public EquipoDTO obtenerEquipoPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null)
            return null;

        // Mapeo manual Entidad -> DTO (para incluir TODOS los detalles)
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setNombre(equipo.getNombre());
        dto.setDescripcion(equipo.getDescripcion());
        dto.setPublico(equipo.isPublico());

        List<MiembroEquipoDTO> miembrosDTO = new ArrayList<>();
        for (MiembroEquipo m : equipo.getMiembros()) {
            MiembroEquipoDTO mDto = new MiembroEquipoDTO();
            mDto.setId(m.getId());
            mDto.setNombrePokemon(m.getNombrePokemon());
            mDto.setMote(m.getMote());
            mDto.setObjeto(m.getObjeto());
            mDto.setHabilidad(m.getHabilidad());
            mDto.setNaturaleza(m.getNaturaleza());
            mDto.setMovimiento1(m.getMovimiento1());
            mDto.setMovimiento2(m.getMovimiento2());
            mDto.setMovimiento3(m.getMovimiento3());
            mDto.setMovimiento4(m.getMovimiento4());
            miembrosDTO.add(mDto);
        }
        dto.setMiembros(miembrosDTO);

        return dto;
    }

    // Método para borrar con seguridad
    public void eliminarEquipo(Long id, String emailUsuario) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        // Verificamos que el equipo pertenezca a quien quiere borrarlo
        if (!equipo.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes permiso para borrar este equipo.");
        }
        
        equipoRepository.deleteById(id);
    }
}