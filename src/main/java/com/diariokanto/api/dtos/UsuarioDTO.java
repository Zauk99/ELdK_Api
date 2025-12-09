package com.diariokanto.api.dtos;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombreCompleto;
    private String username;
    private String fotoPerfilUrl;
    private String email;
    private String pokemonFavorito;
    private String rol;
    private String movil;
    private boolean superAdmin;
    // ¡OJO! Aquí NUNCA ponemos la password
}