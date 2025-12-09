package com.diariokanto.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ...
    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;
    // ... getters y setters (Lombok lo hace solo)

    // Requisito: "limitar a 50 caracteres"
    @Column(length = 50, nullable = false)
    private String nombreCompleto;

    // ...
    @Column(name = "pokemon_favorito")
    private String pokemonFavorito;
    // ...

    @Column(unique = true, nullable = false, length = 15)
    private String username;

    // Requisito: "formato de teléfono español"
    // (La validación estricta de formato se suele hacer en el DTO/Frontend, aquí
    // guardamos el dato)
    private String movil;

    // Requisito: "formato correo electrónico" y único
    @Column(unique = true, nullable = false)
    private String email;

    // Requisito: "Mínimo de dos tipos: administrador y usuario normal"
    private String rol; // Guardaremos "ADMIN" o "USER"

    // Requisito: "contraseña encriptada"
    private String password;

    // Requisito: Tokens para confirmar correo y recuperar contraseña
    private String tokenRecuperacion;
    private String tokenConfirmacion;
    private boolean cuentaConfirmada = false;

    // ... otros campos ...

    @Column(name = "es_super_admin")
    private boolean superAdmin = false; // Por defecto nadie lo es

    public boolean isSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(boolean superAdmin) { this.superAdmin = superAdmin; }
}