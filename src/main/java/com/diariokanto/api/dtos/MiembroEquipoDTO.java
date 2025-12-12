package com.diariokanto.api.dtos;

import lombok.Data;

@Data
public class MiembroEquipoDTO {
    private Long id;
    private String nombrePokemon;
    private String mote;
    private String objeto;
    private String habilidad;
    private String naturaleza;
    
    // Movimientos
    private String movimiento1;
    private String movimiento2;
    private String movimiento3;
    private String movimiento4;

    private int hpEv;
    private int attackEv;
    private int defenseEv;
    private int spAttackEv;
    private int spDefenseEv;
    private int speedEv;
}