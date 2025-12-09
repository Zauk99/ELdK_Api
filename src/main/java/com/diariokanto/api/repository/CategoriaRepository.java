package com.diariokanto.api.repository;

import com.diariokanto.api.entity.Categoria;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Útil para buscar si existe "Videojuegos" antes de crearla
    boolean existsByNombre(String nombre);

    Optional<Categoria> findByNombre(String nombre);
}