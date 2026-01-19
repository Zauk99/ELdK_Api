package com.diariokanto.api.service;

import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.UsuarioRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class TwoFactorService {

    @Autowired private UsuarioRepository usuarioRepository;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // 1. GENERAR SECRETO Y URL QR
    public String generarSecreto() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // 2. VALIDAR CÓDIGO (Para activar o Loguearse)
    public boolean validarCodigo(Usuario usuario, int codigo) {
        // Requisito: Límite de intentos y Bloqueo
        if (usuario.getTiempoBloqueo2FA() != null) {
            if (usuario.getTiempoBloqueo2FA().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Cuenta bloqueada temporalmente por exceso de intentos.");
            } else {
                // Resetear si ya pasó el tiempo
                usuario.setTiempoBloqueo2FA(null);
                usuario.setIntentosFallidos2FA(0);
                usuarioRepository.save(usuario);
            }
        }

        // Validar el código con la librería
        boolean esValido = gAuth.authorize(usuario.getTwoFactorSecret(), codigo);

        if (esValido) {
            // Éxito: Reseteamos contadores
            usuario.setIntentosFallidos2FA(0);
            usuarioRepository.save(usuario);
            return true;
        } else {
            // Fallo: Aumentamos contador
            usuario.setIntentosFallidos2FA(usuario.getIntentosFallidos2FA() + 1);
            if (usuario.getIntentosFallidos2FA() >= 3) {
                // Bloqueo de 1 minuto (Requisito: Expiración/bloqueo)
                usuario.setTiempoBloqueo2FA(LocalDateTime.now().plusMinutes(1));
                usuarioRepository.save(usuario);
                throw new RuntimeException("Código incorrecto. Has excedido los intentos. Espera 1 minuto.");
            }
            usuarioRepository.save(usuario);
            return false;
        }
    }

    // 3. ACTIVAR / DESACTIVAR
    public void activar2FA(Long userId, String secret, int codigoVerificacion) {
        Usuario u = usuarioRepository.findById(userId).orElseThrow();
        // Guardamos temporalmente el secreto para validarlo
        u.setTwoFactorSecret(secret); 
        
        if (validarCodigo(u, codigoVerificacion)) {
            u.setTwoFactorEnabled(true);
            usuarioRepository.save(u);
        } else {
            throw new RuntimeException("Código incorrecto. No se pudo activar el 2FA.");
        }
    }

    public void desactivar2FA(Long userId) {
        Usuario u = usuarioRepository.findById(userId).orElseThrow();
        u.setTwoFactorEnabled(false);
        u.setTwoFactorSecret(null);
        usuarioRepository.save(u);
    }

    public String generarQrUrl(String username, String secret) {
        String appName = "DiarioKanto";
        // Codificamos el nombre y la app para evitar espacios y caracteres raros
        String usuarioCodificado = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String appCodificada = URLEncoder.encode(appName, StandardCharsets.UTF_8);
        
        // Formato estricto para Google Authenticator
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", 
                appCodificada, usuarioCodificado, secret, appCodificada);
    }
}