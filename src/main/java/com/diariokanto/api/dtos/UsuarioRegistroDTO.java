package com.diariokanto.api.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombreCompleto;
    private String username;
    private String email;
    private String password;
    
    @Pattern(regexp = "^[6789]\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9 (Formato España)")
    private String movil;
    private String pokemonFavorito;
}