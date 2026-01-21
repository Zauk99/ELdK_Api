package com.diariokanto.api.controller;

import com.diariokanto.api.dtos.UsuarioDTO;
import com.diariokanto.api.dtos.UsuarioRegistroDTO;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.service.UsuarioService;
import com.diariokanto.api.repository.UsuarioRepository;

import java.io.IOException; // Necesario para la redirección
import jakarta.servlet.http.HttpServletResponse; // Necesario para la respuesta HTTP

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- NUEVO ENDPOINT CONFIRMACIÓN (Que faltaba) ---
    @GetMapping("/confirmar")
    public void confirmarCuenta(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        System.out.println(">>> API: Intentando confirmar cuenta con token: " + token);
        boolean exito = usuarioService.confirmarCuenta(token);

        if (exito) {
            System.out.println(">>> API: ¡Cuenta confirmada con éxito!");
            // Redirigir al login del Frontend con parámetro de éxito
            response.sendRedirect("http://localhost:8080/login?activada=true");
        } else {
            System.err.println(">>> API: Fallo al confirmar (Token inválido o expirado)");
            // Redirigir al login con error
            response.sendRedirect("http://localhost:8080/login?error=token_invalido");
        }
    }

    // --- LOGIN MODIFICADO CON DEBUG ---
    @PostMapping("/login")
    public ResponseEntity<?> validarLogin(@RequestBody UsuarioRegistroDTO loginData) {
        String identificador = loginData.getEmail();
        System.out.println(">>> API LOGIN: Intentando entrar con: " + identificador);

        Usuario usuario = usuarioRepository.findByEmailOrUsername(identificador).orElse(null);

        if (usuario == null) {
            System.err.println(">>> API LOGIN: Usuario NO encontrado en la BD.");
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(loginData.getPassword(), usuario.getPassword())) {
            System.err.println(">>> API LOGIN: Contraseña incorrecta para " + identificador);
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // --- VERIFICACIÓN DE CUENTA CONFIRMADA ---
        if (!usuario.isCuentaConfirmada()) {
            System.err.println(">>> API LOGIN: Usuario correcto pero cuenta NO ACTIVADA.");
            // Devolvemos 403 Forbidden para que el Frontend sepa que es por falta de activación
            return ResponseEntity.status(403).body("Cuenta no confirmada");
        }

        System.out.println(">>> API LOGIN: ¡Login correcto! Devolviendo usuario.");
        return ResponseEntity.ok(usuarioService.buscarPorEmail(usuario.getEmail()));
    }

    // --- RESTO DE MÉTODOS (Igual que tenías) ---

    @GetMapping("/buscar-email")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam String email) {
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

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
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/registro", consumes = { "multipart/form-data" })
    public ResponseEntity<?> registrar(
            @ModelAttribute UsuarioRegistroDTO registroDTO,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.registrarUsuarioConFoto(registroDTO, foto);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/actualizar/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("nombreCompleto") String nombre,
            @RequestParam(value = "pokemonFavorito", required = false) String pokemonFav,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            UsuarioDTO actualizado = usuarioService.actualizarPerfil(id, username, nombre, pokemonFav, foto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> cambiarPassword(@PathVariable Long id, @RequestBody String nuevaPassword) {
        try {
            usuarioService.cambiarPassword(id, nuevaPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(
            @RequestParam(required = false) String buscar,
            Pageable pageable) {
        Page<UsuarioDTO> pagina;
        if (buscar != null && !buscar.isEmpty()) {
            pagina = usuarioService.buscarPaginado(buscar, pageable);
        } else {
            pagina = usuarioService.obtenerTodosPaginado(pageable);
        }
        return ResponseEntity.ok(pagina);
    }
}