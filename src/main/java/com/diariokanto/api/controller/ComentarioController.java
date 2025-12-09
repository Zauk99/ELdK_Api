package com.diariokanto.api.controller;

import com.diariokanto.api.dtos.ComentarioDTO;
import com.diariokanto.api.dtos.CrearComentarioRequest;
import com.diariokanto.api.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    // GET /api/comentarios/noticia/1
    @GetMapping("/noticia/{id}")
    public List<ComentarioDTO> listarPorNoticia(@PathVariable Long id) {
        return comentarioService.obtenerPorNoticia(id);
    }

    /// ... imports (Añade CrearComentarioRequest)

    // POST /api/comentarios/crear
    @PostMapping("/crear")
    public ResponseEntity<ComentarioDTO> crear(@RequestBody CrearComentarioRequest request) {
        try {
            ComentarioDTO nuevoComentario = comentarioService.crearComentario(
                request.getNoticiaId(),
                request.getEmailUsuario(),
                request.getTexto()
            );
            return ResponseEntity.ok(nuevoComentario);
        } catch (RuntimeException e) {
            // Esto evita el error 500 genérico y devuelve un 400 Bad Request más limpio
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/comentarios/{id}
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        comentarioService.eliminarComentario(id);
    }
}