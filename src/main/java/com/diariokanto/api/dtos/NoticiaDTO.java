package com.diariokanto.api.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticiaDTO {
    private Long id;
    private String titulo;
    private String imagenUrl; // Importante para la card
    private String resumen;   // Un trozo del contenido o el contenido entero si prefieres
    private String tagText;   // Ej: "Videojuegos"
    private String tagClass;  // Ej: "videogame-tag"
    private LocalDateTime fechaPublicacion;
    private String nombreCategoria; // Solo el nombre, no el objeto entero
    
    // Campo extra para mostrar el contenido completo en la vista de detalle
    private String contenidoHtml; 
}