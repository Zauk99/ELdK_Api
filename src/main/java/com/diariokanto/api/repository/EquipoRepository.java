package com.diariokanto.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.diariokanto.api.entity.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    // Para ver "Mis Equipos" en el perfil
    List<Equipo> findByUsuario_Id(Long usuarioId);
    
    // Para "Equipos de la Comunidad" (solo los públicos)
    List<Equipo> findAllByPublicoTrueOrderByFechaCreacionDesc();
}