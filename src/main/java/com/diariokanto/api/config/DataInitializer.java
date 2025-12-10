package com.diariokanto.api.config;

import com.diariokanto.api.entity.*;
import com.diariokanto.api.repository.*;
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
                                   EquipoRepository equipoRepo, // <--- Inyectamos el nuevo repo
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. INICIALIZAR CATEGORÍAS
            if (categoriaRepo.count() == 0) {
                categoriaRepo.saveAll(Arrays.asList(
                    crearCategoria("Nintendo"),
                    crearCategoria("Móvil"),
                    crearCategoria("TCG")
                ));
                System.out.println(">>> Categorías creadas automáticamente.");
            }

            // 2. INICIALIZAR USUARIOS
            if (usuarioRepo.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador");
                admin.setFotoPerfilUrl("http://localhost:8080/uploads/default-avatar.png");
                admin.setEmail("admin@diariokanto.com");
                admin.setUsername("adminMaster");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setMovil("+34600000000");
                admin.setCuentaConfirmada(true);
                admin.setSuperAdmin(true);
                
                Usuario user = new Usuario();
                user.setNombreCompleto("Ash Ketchum");
                user.setFotoPerfilUrl("http://localhost:8080/uploads/default-avatar.png");
                user.setEmail("ash@kanto.com");
                user.setUsername("ashKetchum");
                user.setPassword(passwordEncoder.encode("pikachu123"));
                user.setRol("USER");
                user.setMovil("+34600123456");
                user.setCuentaConfirmada(true);
                user.setSuperAdmin(false);

                usuarioRepo.saveAll(Arrays.asList(admin, user));
                System.out.println(">>> Usuarios creados.");
            }

            // 3. INICIALIZAR NOTICIAS
            if (noticiaRepo.count() == 0) {
                Categoria catMovil = categoriaRepo.findByNombre("Móvil").orElse(null);
                Categoria catNintendo = categoriaRepo.findByNombre("Nintendo").orElse(null);

                if (catMovil != null && catNintendo != null) {
                    Noticia n1 = new Noticia();
                    n1.setTitulo("Evento de Mewtwo");
                    n1.setContenidoHtml("<p>Mewtwo regresa a las incursiones...</p>");
                    n1.setImagenUrl("img/mewtwo.jpg");
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

            // 4. INICIALIZAR EQUIPOS (NUEVO BLOQUE)
            if (equipoRepo.count() == 0) {
                Usuario ash = usuarioRepo.findByEmail("ash@kanto.com").orElse(null);

                if (ash != null) {
                    // EQUIPO 1: Campeones de Kanto
                    Equipo team1 = new Equipo();
                    team1.setNombre("Kanto Champions");
                    team1.setDescripcion("Mi equipo clásico para la liga Añil. Estrategia ofensiva pura.");
                    team1.setFechaCreacion(LocalDateTime.now());
                    team1.setUsuario(ash);

                    // Pikachu
                    MiembroEquipo pika = new MiembroEquipo();
                    pika.setPokemonApiId(25L);
                    pika.setNombrePokemon("pikachu");
                    pika.setMote("Chispas");
                    pika.setObjeto("Bola Luminosa");
                    pika.setHabilidad("Elec. Estática");
                    pika.setNaturaleza("Miedosa");
                    pika.setMovimiento1("Rayo");
                    pika.setMovimiento2("Ataque Rápido");
                    pika.setMovimiento3("Cola Férrea");
                    pika.setMovimiento4("Placaje Eléc.");
                    team1.addMiembro(pika);

                    // Charizard
                    MiembroEquipo zard = new MiembroEquipo();
                    zard.setPokemonApiId(6L);
                    zard.setNombrePokemon("charizard");
                    zard.setObjeto("Charizardita Y");
                    zard.setHabilidad("Mar Llamas");
                    zard.setMovimiento1("Lanzallamas");
                    zard.setMovimiento2("Rayo Solar");
                    zard.setMovimiento3("Respiro");
                    zard.setMovimiento4("Onda Certera");
                    team1.addMiembro(zard);
                    
                    // Blastoise (Para que se vean 3)
                    MiembroEquipo blast = new MiembroEquipo();
                    blast.setPokemonApiId(9L);
                    blast.setNombrePokemon("blastoise");
                    team1.addMiembro(blast);

                    equipoRepo.save(team1);

                    // EQUIPO 2: Lluvia
                    Equipo team2 = new Equipo();
                    team2.setNombre("Rain Dance Team");
                    team2.setDescripcion("Equipo basado en el clima de lluvia con Politoed.");
                    team2.setFechaCreacion(LocalDateTime.now().minusDays(5));
                    team2.setUsuario(ash);

                    MiembroEquipo poli = new MiembroEquipo();
                    poli.setPokemonApiId(186L);
                    poli.setNombrePokemon("politoed");
                    poli.setHabilidad("Llovizna");
                    team2.addMiembro(poli);
                    
                    MiembroEquipo swam = new MiembroEquipo();
                    swam.setPokemonApiId(260L);
                    swam.setNombrePokemon("swampert");
                    team2.addMiembro(swam);

                    equipoRepo.save(team2);

                    System.out.println(">>> Equipos de prueba creados.");
                }
            }
        };
    }

    private Categoria crearCategoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return c;
    }
}