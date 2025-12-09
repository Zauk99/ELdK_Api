package com.diariokanto.api.controller;

import com.diariokanto.api.dtos.NoticiaDTO;
import com.diariokanto.api.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    /* // GET /api/noticias
    @GetMapping
    public List<NoticiaDTO> listarTodas() {
        return noticiaService.obtenerTodas();
    } */

    // GET /api/noticias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NoticiaDTO> obtenerPorId(@PathVariable Long id) {
        NoticiaDTO noticia = noticiaService.obtenerPorId(id);
        return noticia != null ? ResponseEntity.ok(noticia) : ResponseEntity.notFound().build();
    }

    // POST /api/noticias/inicializar (Truco para crear datos de prueba rápido)
    @PostMapping("/inicializar")
    public String inicializarDatos() {
        try {
            // Creamos una noticia de ejemplo de cada tipo
            noticiaService.crearNoticia(
                    "Nuevo evento de Mewtwo",
                    "<p>Llega Mewtwo a las incursiones...</p>",
                    "img/mewtwo.jpg",
                    "Móvil");
            noticiaService.crearNoticia(
                    "Lanzamiento Switch 2",
                    "<p>Nintendo anuncia nueva consola...</p>",
                    "img/switch.jpg",
                    "Nintendo");
            return "Datos de prueba creados correctamente";
        } catch (Exception e) {
            return "Error creando datos: " + e.getMessage();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<NoticiaDTO> crearNoticia(@RequestBody NoticiaDTO noticiaDTO) {
        try {
            // El servicio se encarga de buscar la categoría y guardar
            noticiaService.crearNoticia(
                    noticiaDTO.getTitulo(),
                    noticiaDTO.getContenidoHtml(),
                    noticiaDTO.getImagenUrl(),
                    noticiaDTO.getNombreCategoria() // "Nintendo", "Móvil", etc.
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/crear", consumes = {"multipart/form-data"})
    public ResponseEntity<?> crearNoticia(
            @RequestParam("titulo") String titulo,
            @RequestParam("contenido") String contenido,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "fichero", required = false) MultipartFile fichero) {
        try {
            noticiaService.crearNoticiaConImagen(titulo, contenido, categoria, fichero);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al subir imagen");
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarNoticia(
            @PathVariable Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam("contenido") String contenido,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "fichero", required = false) MultipartFile fichero) {
        try {
            noticiaService.actualizarNoticia(id, titulo, contenido, categoria, fichero);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNoticia(@PathVariable Long id) {
        noticiaService.eliminarNoticia(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<NoticiaDTO> listar(@RequestParam(required = false) String busqueda,
                                   @RequestParam(required = false) String categoria) { // <--- Nuevo param
        
        if (busqueda != null && !busqueda.isEmpty()) {
            return noticiaService.buscarPorTitulo(busqueda);
        }
        
        if (categoria != null && !categoria.isEmpty()) {
            return noticiaService.buscarPorCategoria(categoria); // <--- Usamos el nuevo servicio
        }

        return noticiaService.obtenerTodas();
    }
}