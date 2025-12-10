package com.diariokanto.api.controller;

import com.diariokanto.api.dtos.UsuarioDTO;
import com.diariokanto.api.dtos.UsuarioRegistroDTO;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.service.UsuarioService;
import com.diariokanto.api.repository.UsuarioRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET /api/usuarios/buscar-email?email=ejemplo@test.com
    @GetMapping("/buscar-email")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam String email) {
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/usuarios/login (Para verificar contraseña desde el Backend)
    // Recibe un DTO con email y password cruda, devuelve el UsuarioDTO si es
    // correcto
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> validarLogin(@RequestBody UsuarioRegistroDTO loginData) {
        // En loginData.getEmail() nos vendrá el correo O el username (porque usamos ese
        // campo en el JSON)
        String identificador = loginData.getEmail();

        // Usamos el nuevo método del repositorio
        Usuario usuario = usuarioRepository.findByEmailOrUsername(identificador).orElse(null);

        if (usuario != null && passwordEncoder.matches(loginData.getPassword(), usuario.getPassword())) {
            return ResponseEntity.ok(usuarioService.buscarPorEmail(usuario.getEmail()));
        }
        return ResponseEntity.status(401).build();
    }

    // ... dentro de UsuarioController ...

    // GET /api/usuarios
    // GET /api/usuarios -> Listar todos
    @GetMapping
    public List<UsuarioDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    // PUT /api/usuarios/rol/{id} -> Cambiar rol
    @PutMapping("/rol/{id}")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody String nuevoRol) {
        try {
            usuarioService.cambiarRol(id, nuevoRol);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioRegistroDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // POST /api/usuarios/registro
    // Cambiamos a Multipart para aceptar fichero y datos
    @PostMapping(value = "/registro", consumes = { "multipart/form-data" })
    public ResponseEntity<?> registrar(
            @ModelAttribute UsuarioRegistroDTO registroDTO,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        // --- CHIVATOS DE DEBUG ---
        System.out.println(">>> API: Petición de registro recibida para: " + registroDTO.getEmail());

        if (foto == null) {
            System.out.println(">>> API: La foto es NULL");
        } else {
            System.out.println(">>> API: Foto recibida. Nombre: " + foto.getOriginalFilename());
            System.out.println(">>> API: Tamaño: " + foto.getSize() + " bytes");
            System.out.println(">>> API: ¿Está vacía?: " + foto.isEmpty());
        }
        // -------------------------

        try {
            UsuarioDTO nuevoUsuario = usuarioService.registrarUsuarioConFoto(registroDTO, foto);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/usuarios/actualizar/{id}
    // PUT /api/usuarios/actualizar/{id} -> Datos generales
    @PutMapping(value = "/actualizar/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("nombreCompleto") String nombre,
            @RequestParam(value = "pokemonFavorito", required = false) String pokemonFav,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            // Nota: Quitamos 'movil' y 'password' de aquí
            UsuarioDTO actualizado = usuarioService.actualizarPerfil(id, username, nombre, pokemonFav, foto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // PUT /api/usuarios/password/{id} -> Solo contraseña
    @PutMapping("/password/{id}")
    public ResponseEntity<?> cambiarPassword(@PathVariable Long id, @RequestBody String nuevaPassword) {
        try {
            usuarioService.cambiarPassword(id, nuevaPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}