package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "miembros_equipo")
public class MiembroEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardamos el ID de la PokeAPI (ej: 25 para Pikachu)
    // O el nombre si prefieres ("pikachu")
    private Long pokemonApiId; 
    
    private String nombrePokemon; // Guardar el nombre ayuda a no tener que llamar a la API para listas simples

    private String mote; // (Opcional) Ej: "Chispitas"

    // Guardamos los nombres de los objetos y movimientos (o sus IDs)
    private String objeto; // Ej: "leftovers"
    private String habilidad; // Ej: "static"
    private String naturaleza; // Ej: "jolly"

    // Los 4 movimientos
    private String movimiento1;
    private String movimiento2;
    private String movimiento3;
    private String movimiento4;

    // Relación inversa: A qué equipo pertenece
    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;
}