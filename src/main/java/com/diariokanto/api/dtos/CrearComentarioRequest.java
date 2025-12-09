package com.diariokanto.api.dtos;

import lombok.Data;

@Data
public class CrearComentarioRequest {
    private Long noticiaId;
    private String emailUsuario;
    private String texto;
}