package com.diariokanto.api.repository;

import com.diariokanto.api.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    // Obtener comentarios de una noticia específica
    List<Comentario> findByNoticia_Id(Long noticiaId);
}