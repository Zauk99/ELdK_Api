package com.diariokanto.api.controller;

import com.diariokanto.api.service.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired private TwoFactorService twoFactorService;

    // Paso 1: Pedir un nuevo secreto para mostrar el QR
    @GetMapping("/setup/{username}")
    public ResponseEntity<?> setup2FA(@PathVariable String username) {
        String secret = twoFactorService.generarSecreto();
        String qrUrl = twoFactorService.generarQrUrl(username, secret);
        // Devolvemos JSON con secreto y URL
        return ResponseEntity.ok(Map.of("secret", secret, "qrUrl", qrUrl));
    }

    // Paso 2: Confirmar y Activar
    @PostMapping("/activar")
    public ResponseEntity<?> activar(@RequestParam Long userId, @RequestParam String secret, @RequestParam int code) {
        try {
            twoFactorService.activar2FA(userId, secret, code);
            return ResponseEntity.ok("Activado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Paso 3: Validar Login
    @PostMapping("/validar")
    public ResponseEntity<?> validar(@RequestParam Long userId, @RequestParam int code) {
        // Obtenemos usuario (necesitarás inyectar UsuarioService o Repository aquí si quieres buscar por ID primero)
        // Simplificación: Asumimos que el Service busca dentro.
        // *Nota:* En TwoFactorService necesitamos buscar el usuario. Modifica el método validarCodigo para aceptar ID o busca el usuario aquí.
        // CORRECCIÓN RÁPIDA: Inyectamos repo aquí para buscar al usuario y pasarlo.
        return ResponseEntity.badRequest().body("Implementación pendiente en integración"); 
        // (Lo haremos desde el Login real, este endpoint es auxiliar si lo necesitas).
    }
    
    @PostMapping("/desactivar/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        twoFactorService.desactivar2FA(id);
        return ResponseEntity.ok().build();
    }
}