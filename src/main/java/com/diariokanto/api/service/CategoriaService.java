package com.diariokanto.api.service;

import com.diariokanto.api.entity.Categoria;
import com.diariokanto.api.repository.CategoriaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método que se ejecuta automáticamente al iniciar la API
    @PostConstruct
    public void iniciarCategoriasBase() {
        crearSiNoExiste("Nintendo");
        crearSiNoExiste("Móvil");
        crearSiNoExiste("TCG");
    }

    private void crearSiNoExiste(String nombre) {
        if (!categoriaRepository.existsByNombre(nombre)) {
            Categoria c = new Categoria();
            c.setNombre(nombre);
            categoriaRepository.save(c);
        }
    }

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }
}