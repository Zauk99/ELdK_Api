package com.diariokanto.api.config;

import com.diariokanto.api.entity.Categoria;
import com.diariokanto.api.entity.Noticia;
import com.diariokanto.api.entity.Usuario;
import com.diariokanto.api.repository.CategoriaRepository;
import com.diariokanto.api.repository.NoticiaRepository;
import com.diariokanto.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, 
                                   CategoriaRepository categoriaRepo, 
                                   NoticiaRepository noticiaRepo,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. INICIALIZAR CATEGORÍAS (Si no existen)
            if (categoriaRepo.count() == 0) {
                categoriaRepo.saveAll(Arrays.asList(
                    crearCategoria("Nintendo"),
                    crearCategoria("Móvil"),
                    crearCategoria("TCG")
                ));
                System.out.println(">>> Categorías creadas automáticamente.");
            }

            // 2. INICIALIZAR USUARIOS (Si no existen)
            if (usuarioRepo.count() == 0) {
                // Crear ADMIN
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador");
                admin.setFotoPerfilUrl("http://localhost:8080/uploads/default-avatar.png");
                admin.setEmail("admin@diariokanto.com");
                admin.setUsername("adminMaster");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setMovil("+34600000000");
                admin.setCuentaConfirmada(true);
                admin.setSuperAdmin(true); // El admin principal es super admin
                
                // Crear USER normal
                Usuario user = new Usuario();
                user.setNombreCompleto("Ash Ketchum");
                user.setFotoPerfilUrl("http://localhost:8080/uploads/default-avatar.png");
                user.setEmail("ash@kanto.com");
                user.setUsername("ashKetchum");
                user.setPassword(passwordEncoder.encode("pikachu123"));
                user.setRol("USER");
                user.setMovil("+34600123456");
                user.setCuentaConfirmada(true);
                user.setSuperAdmin(false); // El usuario normal no es super admin

                usuarioRepo.saveAll(Arrays.asList(admin, user));
                System.out.println(">>> Usuarios creados (Admin: admin@diariokanto.com / admin123).");
            }

            // 3. INICIALIZAR NOTICIAS (Si no existen)
            if (noticiaRepo.count() == 0) {
                Categoria catMovil = categoriaRepo.findByNombre("Móvil").orElse(null);
                Categoria catNintendo = categoriaRepo.findByNombre("Nintendo").orElse(null);

                if (catMovil != null && catNintendo != null) {
                    Noticia n1 = new Noticia();
                    n1.setTitulo("Evento de Mewtwo");
                    n1.setContenidoHtml("<p>Mewtwo regresa a las incursiones...</p>");
                    n1.setImagenUrl("img/mewtwo.jpg"); // Asegúrate de tener esta imagen en static del frontend
                    n1.setFechaPublicacion(LocalDateTime.now());
                    n1.setCategoria(catMovil);
                    n1.setTagText("Móvil");
                    n1.setTagClass("mobile-tag");

                    Noticia n2 = new Noticia();
                    n2.setTitulo("Switch 2 Anunciada");
                    n2.setContenidoHtml("<p>Nintendo sorprende con su nueva consola...</p>");
                    n2.setImagenUrl("img/switch.jpg");
                    n2.setFechaPublicacion(LocalDateTime.now().minusDays(1));
                    n2.setCategoria(catNintendo);
                    n2.setTagText("Nintendo");
                    n2.setTagClass("videogame-tag");

                    noticiaRepo.saveAll(Arrays.asList(n1, n2));
                    System.out.println(">>> Noticias de prueba creadas.");
                }
            }
        };
    }

    // Helper para crear categorías rápido
    private Categoria crearCategoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return c;
    }
}