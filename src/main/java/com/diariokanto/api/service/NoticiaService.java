package com.diariokanto.api.service;

import com.diariokanto.api.dtos.NoticiaDTO;
import com.diariokanto.api.entity.Categoria;
import com.diariokanto.api.entity.Noticia;
import com.diariokanto.api.repository.CategoriaRepository;
import com.diariokanto.api.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;

@Service
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<NoticiaDTO> obtenerTodas() {
        return noticiaRepository.findAllByOrderByFechaPublicacionDesc().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    public NoticiaDTO obtenerPorId(Long id) {
        return noticiaRepository.findById(id)
                .map(this::mapearADTO)
                .orElse(null);
    }

    // Método para crear noticias desde el admin (útil para inicializar datos)
    public void crearNoticia(String titulo, String contenido, String imagen, String nombreCategoria) {
        Categoria cat = categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreCategoria))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + nombreCategoria));

        Noticia n = new Noticia();
        n.setTitulo(titulo);
        n.setContenidoHtml(contenido);
        n.setImagenUrl(imagen);
        n.setFechaPublicacion(LocalDateTime.now());
        n.setCategoria(cat);

        // Lógica visual para las etiquetas del frontend antiguo
        n.setTagText(cat.getNombre()); // "Videojuegos"
        if (nombreCategoria.equalsIgnoreCase("Móvil"))
            n.setTagClass("mobile-tag");
        else if (nombreCategoria.equalsIgnoreCase("TCG"))
            n.setTagClass("tcg-tag");
        else
            n.setTagClass("videogame-tag");

        noticiaRepository.save(n);
    }

    private NoticiaDTO mapearADTO(Noticia n) {
        NoticiaDTO dto = new NoticiaDTO();
        dto.setId(n.getId());
        dto.setTitulo(n.getTitulo());
        dto.setImagenUrl(n.getImagenUrl());
        dto.setFechaPublicacion(n.getFechaPublicacion());
        dto.setContenidoHtml(n.getContenidoHtml());
        // Generamos un resumen simple quitando etiquetas HTML
        String textoPlano = n.getContenidoHtml().replaceAll("\\<.*?\\>", "");
        dto.setResumen(textoPlano.length() > 100 ? textoPlano.substring(0, 100) + "..." : textoPlano);

        dto.setTagText(n.getTagText());
        dto.setTagClass(n.getTagClass());
        dto.setNombreCategoria(n.getCategoria().getNombre());
        return dto;
    }

    public void crearNoticiaConImagen(String titulo, String contenido, String categoria, MultipartFile archivo)
            throws IOException {
        // 1. Guardar la imagen en disco
        String nombreImagen = "default.jpg"; // Imagen por defecto

        if (archivo != null && !archivo.isEmpty()) {
            // Creamos nombre único para no machacar fotos: "uuid_nombre.jpg"
            String nombreUnico = java.util.UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");

            // Crear carpeta si no existe
            if (!Files.exists(rutaUploads)) {
                Files.createDirectories(rutaUploads);
            }

            // Guardar bytes
            Files.copy(archivo.getInputStream(), rutaUploads.resolve(nombreUnico));

            // La URL que guardamos en BBDD apunta a nuestro endpoint de recursos estáticos
            // IMPORTANTE: Ponemos la URL completa de la API (8080)
            nombreImagen = "http://localhost:8080/uploads/" + nombreUnico;
        }

        // 2. Crear Noticia (igual que antes)
        crearNoticia(titulo, contenido, nombreImagen, categoria);
    }

    // UPDATE
    public void actualizarNoticia(Long id, String titulo, String contenido, String nombreCategoria,
            MultipartFile archivo) throws IOException {
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));

        noticia.setTitulo(titulo);
        noticia.setContenidoHtml(contenido);

        // Actualizar Categoría
        Categoria cat = categoriaRepository.findByNombre(nombreCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        noticia.setCategoria(cat);
        noticia.setTagText(cat.getNombre());
        // Lógica de tagClass (copia la que tenías en crearNoticia)
        if (nombreCategoria.equalsIgnoreCase("Móvil"))
            noticia.setTagClass("mobile-tag");
        else if (nombreCategoria.equalsIgnoreCase("TCG"))
            noticia.setTagClass("tcg-tag");
        else
            noticia.setTagClass("videogame-tag");

        // Actualizar Imagen (Solo si viene una nueva)
        if (archivo != null && !archivo.isEmpty()) {
            // ... (Misma lógica de guardar archivo que en crearNoticiaConImagen) ...
            String nombreUnico = java.util.UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads))
                Files.createDirectories(rutaUploads);
            Files.copy(archivo.getInputStream(), rutaUploads.resolve(nombreUnico));

            noticia.setImagenUrl("http://localhost:8080/uploads/" + nombreUnico);
        }

        noticiaRepository.save(noticia);
    }

    // DELETE
    public void eliminarNoticia(Long id) {
        // Opcional: Borrar la imagen del disco aquí si quisieras ser muy limpio
        noticiaRepository.deleteById(id);
    }

    public List<NoticiaDTO> buscarPorTitulo(String texto) {
        return noticiaRepository.findByTituloContainingIgnoreCase(texto).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

}