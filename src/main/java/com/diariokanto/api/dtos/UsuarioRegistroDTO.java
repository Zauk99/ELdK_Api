package com.diariokanto.api.dtos;

import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class UsuarioRegistroDTO {
    private String nombreCompleto;
    private String username;
    private String email;
    private String password;
    
    @Pattern(regexp = "^[6789]\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9 (Formato España)")
    private String movil;
    private String pokemonFavorito;
}