package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Ej: "Videojuegos", "TCG", "Anime"

    // Opcional: Para saber qué noticias tiene una categoría
    // @JsonIgnore para evitar bucles infinitos al convertir a JSON
    // @OneToMany(mappedBy = "categoria")
    // private List<Noticia> noticias;
}