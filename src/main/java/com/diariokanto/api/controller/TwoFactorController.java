package com.diariokanto.api.controller;

import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.UsuarioRepository;
import com.diariokanto.api.service.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired private TwoFactorService twoFactorService;
    @Autowired private UsuarioRepository usuarioRepository; // Necesario para buscar usuario fresco

    @GetMapping("/setup/{username}")
    public ResponseEntity<?> setup2FA(@PathVariable String username) {
        String secret = twoFactorService.generarSecreto();
        String qrUrl = twoFactorService.generarQrUrl(username, secret);
        return ResponseEntity.ok(Map.of("secret", secret, "qrUrl", qrUrl));
    }

    @PostMapping("/activar")
    public ResponseEntity<?> activar(@RequestParam Long userId, @RequestParam String secret, @RequestParam int code) {
        try {
            twoFactorService.activar2FA(userId, secret, code);
            return ResponseEntity.ok("Activado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/desactivar/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        twoFactorService.desactivar2FA(id);
        return ResponseEntity.ok().build();
    }

    // --- EL MÉTODO IMPORTANTE (LOGIN) ---
    @PostMapping("/validar")
    public ResponseEntity<?> validar(@RequestParam Long userId, @RequestParam int code) {
        // 1. Buscamos al usuario FRESCO de la BD (para ver sus intentos y secreto actuales)
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            boolean esValido = twoFactorService.validarCodigo(usuario, code);
            
            if (esValido) {
                return ResponseEntity.ok().build(); // 200 OK
            } else {
                // Si devuelve false, forzamos error 401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto");
            }
            
        } catch (RuntimeException e) {
            // Si salta excepción (por bloqueo), devolvemos 403 o 429
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}