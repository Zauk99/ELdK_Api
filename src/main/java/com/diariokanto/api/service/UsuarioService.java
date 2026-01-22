package com.diariokanto.api.service;

import com.diariokanto.api.dtos.UsuarioDTO;
import com.diariokanto.api.dtos.UsuarioRegistroDTO;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.ComentarioRepository;
import com.diariokanto.api.repository.EquipoRepository;
import com.diariokanto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private EmailService emailService;

    // ==========================================
    // NUEVOS MÉTODOS DE PAGINACIÓN
    // ==========================================

    // 1. Obtener TODOS los usuarios paginados
    public Page<UsuarioDTO> obtenerTodosPaginado(Pageable pageable) {
        // .map(this::mapearADTO) convierte automáticamente cada Entidad de la página a
        // DTO
        return usuarioRepository.findAll(pageable).map(this::mapearADTO);
    }

    // 2. BUSCAR usuarios paginados
    public Page<UsuarioDTO> buscarPaginado(String busqueda, Pageable pageable) {
        // CORRECCIÓN: Llamamos al nuevo método con 'NombreCompleto'
        return usuarioRepository
                .findByNombreCompletoContainingIgnoreCaseOrUsernameContainingIgnoreCase(busqueda, busqueda, pageable)
                .map(this::mapearADTO);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO
    // ==========================================

    public UsuarioDTO registrarUsuario(UsuarioRegistroDTO registroDTO) {
        validarRegistro(registroDTO);

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setMovil(registroDTO.getMovil());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol("USER");
        usuario.setTokenConfirmacion(UUID.randomUUID().toString());
        usuario.setCuentaConfirmada(false);

        Usuario guardado = usuarioRepository.save(usuario);

        // ENVIAR CORREO REAL
        try {
            emailService.enviarCorreoConfirmacion(guardado.getEmail(), guardado.getTokenConfirmacion());
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
            // No lanzamos error para no cortar el registro, pero queda en el log
        }

        return mapearADTO(guardado);
    }

    public UsuarioDTO registrarUsuarioConFoto(UsuarioRegistroDTO registroDTO, MultipartFile foto) throws IOException {
        validarRegistro(registroDTO);

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setMovil(registroDTO.getMovil());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol("USER");
        usuario.setPokemonFavorito(registroDTO.getPokemonFavorito());
        usuario.setTokenConfirmacion(UUID.randomUUID().toString());
        usuario.setCuentaConfirmada(false);

        // Lógica de Foto
        String urlFoto = "http://localhost:8080/uploads/default-avatar.png";
        if (foto != null && !foto.isEmpty()) {
            String nombreUnico = "user_" + UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads))
                Files.createDirectories(rutaUploads);
            Files.copy(foto.getInputStream(), rutaUploads.resolve(nombreUnico));
            urlFoto = "http://localhost:8080/uploads/" + nombreUnico;
        }
        usuario.setFotoPerfilUrl(urlFoto);

        Usuario guardado = usuarioRepository.save(usuario);

        // ENVIAR CORREO REAL
        try {
            emailService.enviarCorreoConfirmacion(guardado.getEmail(), guardado.getTokenConfirmacion());
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
            // No lanzamos error para no cortar el registro, pero queda en el log
        }

        return mapearADTO(guardado);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::mapearADTO)
                .orElse(null);
    }

    public UsuarioDTO actualizarUsuario(Long id, UsuarioRegistroDTO datosNuevos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombreCompleto(datosNuevos.getNombreCompleto());
        usuario.setMovil(datosNuevos.getMovil());

        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
        }

        return mapearADTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO actualizarPerfil(Long id, String username, String nombreCompleto, String pokemonFav,
            MultipartFile foto) throws IOException {

        // VALIDACIÓN MANUAL EXTRA
        if (nombreCompleto != null && nombreCompleto.length() > 50) {
            throw new RuntimeException("El nombre no puede superar los 50 caracteres.");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getUsername().equals(username)) {
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El nombre de usuario '" + username + "' ya está en uso.");
            }
            usuario.setUsername(username);
        }

        usuario.setNombreCompleto(nombreCompleto);
        usuario.setPokemonFavorito(pokemonFav);

        if (foto != null && !foto.isEmpty()) {
            String nombreUnico = "user_" + id + "_" + System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads))
                Files.createDirectories(rutaUploads);
            Files.copy(foto.getInputStream(), rutaUploads.resolve(nombreUnico), StandardCopyOption.REPLACE_EXISTING);
            usuario.setFotoPerfilUrl("http://localhost:8080/uploads/" + nombreUnico);
        }

        return mapearADTO(usuarioRepository.save(usuario));
    }

    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    // Listar todos (Legacy - para dropdowns o cosas sin paginación)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    public void cambiarRol(Long id, String nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.isSuperAdmin()) {
            throw new RuntimeException("OPERACIÓN DENEGADA: Este Administrador está protegido.");
        }

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    // DELETE (Con protecciones)
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Protecciones (Super Admin y Último Admin)
        if (usuario.isSuperAdmin()) {
            throw new RuntimeException("No se puede eliminar al Super Administrador principal.");
        }
        if ("ADMIN".equals(usuario.getRol())) {
            long adminCount = usuarioRepository.findAll().stream()
                    .filter(u -> "ADMIN".equals(u.getRol()))
                    .count();
            if (adminCount <= 1) {
                throw new RuntimeException("No puedes eliminar tu cuenta porque eres el último administrador.");
            }
        }

        // 2. Borrado en cascada manual
        comentarioRepository.deleteByUsuarioId(id);
        equipoRepository.deleteByUsuarioId(id);

        // 3. Borrado final
        usuarioRepository.deleteById(id);
    }

    // ==========================================
    // MÉTODOS PRIVADOS
    // ==========================================

    private void validarRegistro(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (dto.getUsername().length() < 7 || dto.getUsername().length() > 15) {
            throw new RuntimeException("El usuario debe tener entre 7 y 15 caracteres");
        }
    }

    // --- NUEVO MÉTODO PARA ACTIVAR CUENTA ---
    public boolean confirmarCuenta(String token) {
        // Buscamos al usuario por el token
        Usuario usuario = usuarioRepository.findByTokenConfirmacion(token)
                .orElse(null);

        if (usuario == null) {
            return false; // Token no válido
        }

        // Activamos la cuenta
        usuario.setCuentaConfirmada(true);
        usuario.setTokenConfirmacion(null); // Borramos el token para que no se use dos veces
        usuarioRepository.save(usuario);
        
        return true;
    }

    // 1. SOLICITAR RECUPERACIÓN (Genera token y envía email)
    public boolean solicitarRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        
        if (usuario != null) {
            // Generamos token único
            String token = UUID.randomUUID().toString();
            usuario.setTokenRecuperacion(token);
            usuarioRepository.save(usuario); // Guardamos el token en la BD
            
            // Enviamos el correo
            emailService.enviarCorreoRecuperacion(usuario.getEmail(), token);
            return true;
        }
        return false; // El usuario no existe (por seguridad no solemos decir esto al frontend, pero internamente lo controlamos)
    }

    // 2. RESTABLECER CONTRASEÑA (Recibe token y nueva pass)
    public boolean restablecerPassword(String token, String nuevaPassword) {
        // Buscamos usuario con ese token de recuperación (necesitas añadir este método al repositorio, ver paso 3)
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token).orElse(null);

        if (usuario == null) {
            return false; // Token inválido
        }

        // Actualizamos password y borramos token
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null); 
        usuarioRepository.save(usuario);
        
        return true;
    }

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
        dto.setTwoFactorEnabled(u.isTwoFactorEnabled());
        return dto;
    }
}