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
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("Diario de Kanto <noreply@diariokanto.com>");
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido Entrenador! Confirma tu cuenta");
        mensaje.setText("Hola,\n\n" +
                "Gracias por registrarte en El Diario de Kanto.\n" +
                "Para activar tu cuenta, por favor haz clic en el siguiente enlace:\n\n" +
                "http://localhost:8081/confirmar?token=" + token + "\n\n" +
                "Si no has sido tú, ignora este mensaje.\n\n" +
                "¡Hazte con todos!");

        mailSender.send(mensaje);
        System.out.println("📧 Correo de confirmación enviado a: " + destinatario);
    }
}