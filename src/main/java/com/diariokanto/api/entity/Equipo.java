package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Ej: "Team Rocket Remake"

    @Column(columnDefinition = "TEXT")
    private String descripcion; // Ej: "Estrategia basada en veneno..."

    private LocalDateTime fechaCreacion;

    // Relación con el Usuario (Un usuario tiene muchos equipos)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con los Pokémon (Un equipo tiene hasta 6 miembros)
    // 'cascade = ALL' significa que si borras el equipo, se borran sus miembros
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MiembroEquipo> miembros = new ArrayList<>();
    
    // Método helper para añadir miembros fácilmente
    public void addMiembro(MiembroEquipo miembro) {
        miembros.add(miembro);
        miembro.setEquipo(this);
    }
}