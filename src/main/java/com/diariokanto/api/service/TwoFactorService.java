package com.diariokanto.api.service;

import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.UsuarioRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TwoFactorService {

    @Autowired private UsuarioRepository usuarioRepository;

    // CONFIGURACIÓN MÁS TOLERANTE (WindowSize 3 = permite un margen de error de 90 segundos)
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generarSecreto() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Método corregido con URLEncoder (ya lo tenías bien del paso anterior)
    public String generarQrUrl(String username, String secret) {
        String appName = "DiarioKanto";
        try {
            String usuarioCodificado = java.net.URLEncoder.encode(username, "UTF-8");
            String appCodificada = java.net.URLEncoder.encode(appName, "UTF-8");
            return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", 
                    appCodificada, usuarioCodificado, secret, appCodificada);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean validarCodigo(Usuario usuario, int codigo) {
        // 1. CHEQUEO DE BLOQUEO
        if (usuario.getTiempoBloqueo2FA() != null) {
            if (usuario.getTiempoBloqueo2FA().isAfter(LocalDateTime.now())) {
                System.out.println(">>> 2FA BLOQUEADO: Usuario " + usuario.getUsername());
                throw new RuntimeException("Cuenta bloqueada. Espera 1 minuto.");
            } else {
                usuario.setTiempoBloqueo2FA(null);
                usuario.setIntentosFallidos2FA(0);
                usuarioRepository.save(usuario);
            }
        }

        // 2. VALIDACIÓN
        // IMPORTANTE: Imprimimos logs para depurar
        System.out.println(">>> VALIDANDO 2FA para: " + usuario.getUsername());
        System.out.println(">>> Secreto en BD: " + usuario.getTwoFactorSecret());
        System.out.println(">>> Código recibido: " + codigo);

        boolean esValido = gAuth.authorize(usuario.getTwoFactorSecret(), codigo);

        if (esValido) {
            System.out.println(">>> CÓDIGO CORRECTO");
            if (usuario.getIntentosFallidos2FA() > 0) {
                usuario.setIntentosFallidos2FA(0);
                usuarioRepository.save(usuario);
            }
            return true;
        } else {
            System.out.println(">>> CÓDIGO INCORRECTO");
            
            // 3. GESTIÓN DE FALLOS (Incrementar y Guardar SIEMPRE)
            int nuevosIntentos = usuario.getIntentosFallidos2FA() + 1;
            usuario.setIntentosFallidos2FA(nuevosIntentos);
            
            if (nuevosIntentos >= 5) {
                System.out.println(">>> LÍMITE SUPERADO (5 intentos). Bloqueando.");
                usuario.setTiempoBloqueo2FA(LocalDateTime.now().plusMinutes(1));
                usuarioRepository.save(usuario); // Guardamos bloqueo
                throw new RuntimeException("Límite de intentos superado. Bloqueado 1 min.");
            }

            usuarioRepository.save(usuario); // Guardamos incremento
            return false;
        }
    }
    
    // Método activar2FA (necesario para el primer setup)
    public void activar2FA(Long userId, String secret, int codigoVerificacion) {
        Usuario u = usuarioRepository.findById(userId).orElseThrow();
        
        // Validamos contra el secreto NUEVO que nos mandan, no el de la BD
        boolean esValido = gAuth.authorize(secret, codigoVerificacion);

        if (esValido) {
            u.setTwoFactorSecret(secret);
            u.setTwoFactorEnabled(true);
            u.setIntentosFallidos2FA(0);
            u.setTiempoBloqueo2FA(null);
            usuarioRepository.save(u);
        } else {
            throw new RuntimeException("Código incorrecto. No se pudo activar.");
        }
    }
    
    public void desactivar2FA(Long userId) {
        Usuario u = usuarioRepository.findById(userId).orElseThrow();
        u.setTwoFactorEnabled(false);
        u.setTwoFactorSecret(null);
        usuarioRepository.save(u);
    }
}