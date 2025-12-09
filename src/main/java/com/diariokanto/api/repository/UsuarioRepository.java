package com.diariokanto.api.repository;

import com.diariokanto.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Para el login
    Optional<Usuario> findByEmail(String email);
    
    // Para validaciones de registro (evitar duplicados)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Para confirmar cuenta por token
    Optional<Usuario> findByTokenConfirmacion(String token);

    // Para el Login (busca por email O por username)
    @Query("SELECT u FROM Usuario u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<Usuario> findByEmailOrUsername(@Param("identifier") String identifier);
}