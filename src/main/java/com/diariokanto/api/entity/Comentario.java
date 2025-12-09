package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    private LocalDateTime fechaHora;

    // Relación: Quién escribió el comentario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación: En qué noticia
    @ManyToOne
    @JoinColumn(name = "noticia_id", nullable = false)
    private Noticia noticia;
}