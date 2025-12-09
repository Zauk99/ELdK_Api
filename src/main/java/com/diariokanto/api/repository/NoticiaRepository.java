package com.diariokanto.api.repository;

import com.diariokanto.api.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    // Para filtrar noticias por categoría (Requisito de interfaz)
    List<Noticia> findByCategoria_Id(Long categoriaId);

    // 👇 NUEVO: Busca por el nombre de la categoría relacionada
    List<Noticia> findByCategoria_Nombre(String nombreCategoria);
    
    // Para ordenar las noticias por fecha (las nuevas primero)
    List<Noticia> findAllByOrderByFechaPublicacionDesc();

    // Busca noticias cuyo título contenga el texto (ignorando mayúsculas/minúsculas)
    List<Noticia> findByTituloContainingIgnoreCase(String texto);
}