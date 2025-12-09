package com.diariokanto.api.dtos;

import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String nombreCompleto;
    private String username;
    private String email;
    private String password;
    private String movil;
}