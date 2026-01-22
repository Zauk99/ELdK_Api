package com.diariokanto.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Método para enviar correo de confirmación
    public void enviarCorreoConfirmacion(String destinatario, String token) {

        String url = "http://localhost:8080/api/usuarios/confirmar?token=" + token; 
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("Diario de Kanto <noreply@diariokanto.com>");
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido Entrenador! Confirma tu cuenta");
        mensaje.setText("Hola,\n\n" +
                "Gracias por registrarte en El Diario de Kanto.\n" +
                "Para activar tu cuenta, por favor haz clic en el siguiente enlace:\n\n" +
                url + "\n\n" +
                "Si no has sido tú, ignora este mensaje.\n\n" +
                "¡Hazte con todos!");

        mailSender.send(mensaje);
        System.out.println("📧 Correo de confirmación enviado a: " + destinatario);
    }

    // AÑADE ESTE MÉTODO DEBAJO DEL DE CONFIRMACIÓN
    public void enviarCorreoRecuperacion(String destinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("Diario de Kanto <noreply@diariokanto.com>");
        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de Contraseña");
        
        // Fíjate que apunta al puerto 8081 (WEB) a una ruta nueva "/restablecer"
        String url = "http://localhost:8081/restablecer?token=" + token;

        mensaje.setText("Hola entrenador,\n\n" +
                "Hemos recibido una solicitud para restablecer tu contraseña.\n" +
                "Si has sido tú, pulsa en el siguiente enlace:\n\n" +
                url + "\n\n" +
                "Este enlace es válido por 24 horas.\n" +
                "Si no has sido tú, por favor ignora este correo.");

        mailSender.send(mensaje);
        System.out.println("📧 Correo de recuperación enviado a: " + destinatario);
    }
}