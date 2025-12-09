package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "noticias")
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    
    // TEXT para permitir contenido HTML largo del CKEditor o similar
    @Column(columnDefinition = "TEXT")
    private String contenidoHtml;

    private String imagenUrl; // URL de la imagen de portada
    
    private LocalDateTime fechaPublicacion;

    // Relación con Categoría (Muchas noticias -> Una categoría)
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    // Etiquetas visuales para el frontend (ej: "mobile-tag")
    private String tagClass;
    private String tagText;
}