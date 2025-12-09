package com.diariokanto.api.service;

import com.diariokanto.api.dtos.UsuarioDTO;
import com.diariokanto.api.dtos.UsuarioRegistroDTO;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Necesitas configurar esto en una clase de config

    public UsuarioDTO registrarUsuario(UsuarioRegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        // Validar longitud (7-15)
        if (registroDTO.getUsername().length() < 7 || registroDTO.getUsername().length() > 15) {
            throw new RuntimeException("El usuario debe tener entre 7 y 15 caracteres");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setMovil(registroDTO.getMovil());
        
        // Encriptar contraseña (REQUISITO)
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        
        // Asignar rol por defecto
        usuario.setRol("USER");
        
        // Generar token de confirmación (REQUISITO)
        usuario.setTokenConfirmacion(UUID.randomUUID().toString());
        usuario.setCuentaConfirmada(false);

        Usuario guardado = usuarioRepository.save(usuario);
        
        // AQUÍ SIMULAMOS EL ENVÍO DE CORREO (Log)
        System.out.println("--- SIMULACIÓN EMAIL ---");
        System.out.println("Para: " + guardado.getEmail());
        System.out.println("Link: http://localhost:8081/confirmar?token=" + guardado.getTokenConfirmacion());
        System.out.println("------------------------");

        return mapearADTO(guardado);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::mapearADTO)
                .orElse(null);
    }
    
    // Método auxiliar para convertir Entidad -> DTO
    private UsuarioDTO mapearADTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setFotoPerfilUrl(u.getFotoPerfilUrl());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPokemonFavorito(u.getPokemonFavorito()); 
        dto.setRol(u.getRol());
        dto.setMovil(u.getMovil());
        dto.setSuperAdmin(u.isSuperAdmin());
        return dto;
    }

    // UPDATE
    public UsuarioDTO actualizarUsuario(Long id, UsuarioRegistroDTO datosNuevos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombreCompleto(datosNuevos.getNombreCompleto());
        usuario.setMovil(datosNuevos.getMovil());
        
        // Solo actualizamos contraseña si viene una nueva (no está vacía)
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
        }
        
        // El email y rol normalmente no se dejan cambiar tan fácil, pero podrías añadirlos aquí
        
        return mapearADTO(usuarioRepository.save(usuario));
    }

    // DELETE
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    // Modifica el método registrarUsuario (o crea uno nuevo si prefieres mantener el viejo por seguridad, pero mejor modificarlo)
    // Ahora aceptará MultipartFile
    public UsuarioDTO registrarUsuarioConFoto(UsuarioRegistroDTO registroDTO, MultipartFile foto) throws IOException {
        
        // 1. VALIDACIONES (Importante: Hacerlas antes de procesar nada)
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        // Validar longitud (7-15)
        if (registroDTO.getUsername().length() < 7 || registroDTO.getUsername().length() > 15) {
            throw new RuntimeException("El usuario debe tener entre 7 y 15 caracteres");
        }

        // 2. CREACIÓN DEL USUARIO
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setMovil(registroDTO.getMovil());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol("USER");
        
        // 3. TOKEN Y ESTADO
        usuario.setTokenConfirmacion(UUID.randomUUID().toString());
        usuario.setCuentaConfirmada(false); 

        // 4. LÓGICA DE FOTO
        String urlFoto = "http://localhost:8080/uploads/default-avatar.png"; // Imagen por defecto

        if (foto != null && !foto.isEmpty()) {
            // Generamos nombre único para evitar colisiones
            String nombreUnico = "user_" + UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            
            if (!Files.exists(rutaUploads)) Files.createDirectories(rutaUploads);
            
            Files.copy(foto.getInputStream(), rutaUploads.resolve(nombreUnico));
            urlFoto = "http://localhost:8080/uploads/" + nombreUnico;
        }
        
        usuario.setFotoPerfilUrl(urlFoto);

        // 5. GUARDADO EN BBDD
        Usuario guardado = usuarioRepository.save(usuario);

        // 6. SIMULACIÓN EMAIL (Para ver el token en consola)
        System.out.println("--- SIMULACIÓN EMAIL (REGISTRO CON FOTO) ---");
        System.out.println("Para: " + guardado.getEmail());
        System.out.println("Link: http://localhost:8081/confirmar?token=" + guardado.getTokenConfirmacion());
        System.out.println("--------------------------------------------");

        return mapearADTO(guardado);
    }

    // Método para actualizar perfil
    public UsuarioDTO actualizarPerfil(Long id, String username, String nombreCompleto, String pokemonFav, MultipartFile foto) throws IOException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. VALIDACIÓN DE USERNAME ÚNICO
        // Solo verificamos si el usuario ha cambiado el nombre (si es el mismo, no hacemos nada)
        if (!usuario.getUsername().equals(username)) {
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El nombre de usuario '" + username + "' ya está en uso.");
            }
            usuario.setUsername(username);
        }

        // 2. ACTUALIZACIÓN DE DATOS (Móvil y Password ya NO se tocan aquí)
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setPokemonFavorito(pokemonFav);

        // 3. ACTUALIZACIÓN DE FOTO
        if (foto != null && !foto.isEmpty()) {
            String nombreUnico = "user_" + id + "_" + System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads)) Files.createDirectories(rutaUploads);
            Files.copy(foto.getInputStream(), rutaUploads.resolve(nombreUnico), StandardCopyOption.REPLACE_EXISTING);
            usuario.setFotoPerfilUrl("http://localhost:8080/uploads/" + nombreUnico);
        }

        return mapearADTO(usuarioRepository.save(usuario));
    }
    
    // Método separado SOLO para cambiar contraseña
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    // 1. Listar todos los usuarios
    public List<UsuarioDTO> listarTodos() {
        // CAMBIO AQUÍ: Añadimos Sort.by(...) dentro del findAll
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    // 2. Actualizar la protección en cambiarRol
    public void cambiarRol(Long id, String nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // PROTECCIÓN ROBUSTA: Si tiene la marca de Super Admin, nadie lo toca
        if (usuario.isSuperAdmin()) {
            throw new RuntimeException("OPERACIÓN DENEGADA: Este Administrador está protegido y no puede ser modificado.");
        }

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

}