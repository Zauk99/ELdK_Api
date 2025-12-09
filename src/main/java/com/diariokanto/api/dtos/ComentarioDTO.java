package com.diariokanto.api.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComentarioDTO {
    private Long id;
    private String texto;
    private String autorNombre; // Solo el nombre del usuario
    private LocalDateTime fecha;
}