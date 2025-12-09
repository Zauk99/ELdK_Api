package com.diariokanto.api.service;

import com.diariokanto.api.dtos.ComentarioDTO;
import com.diariokanto.api.entity.Comentario;
import com.diariokanto.api.entity.Noticia;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.ComentarioRepository;
import com.diariokanto.api.repository.NoticiaRepository;
import com.diariokanto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private NoticiaRepository noticiaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ComentarioDTO> obtenerPorNoticia(Long noticiaId) {
        return comentarioRepository.findByNoticia_Id(noticiaId).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    public ComentarioDTO crearComentario(Long noticiaId, String emailUsuario, String texto) {
        Noticia noticia = noticiaRepository.findById(noticiaId)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comentario c = new Comentario();
        c.setTexto(texto);
        c.setFechaHora(LocalDateTime.now());
        c.setNoticia(noticia);
        c.setUsuario(usuario);

        return mapearADTO(comentarioRepository.save(c));
    }
    
    public void eliminarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }

    private ComentarioDTO mapearADTO(Comentario c) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(c.getId());
        dto.setTexto(c.getTexto());
        dto.setAutorNombre(c.getUsuario().getNombreCompleto());
        dto.setFecha(c.getFechaHora());
        return dto;
    }
}