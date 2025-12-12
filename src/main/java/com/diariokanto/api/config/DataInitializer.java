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
            EquipoRepository equipoRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. INICIALIZAR CATEGORÍAS
            if (categoriaRepo.count() == 0) {
                categoriaRepo.saveAll(Arrays.asList(
                        crearCategoria("Nintendo"),
                        crearCategoria("Móvil"),
                        crearCategoria("TCG")));
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

            // 4. INICIALIZAR EQUIPOS COMPLETOS
            // Descomenta la siguiente línea si quieres borrar los equipos viejos al reiniciar:
            // equipoRepo.deleteAll(); 
            
            if (equipoRepo.count() == 0) {
                Usuario admin = usuarioRepo.findByEmail("admin@diariokanto.com").orElse(null);
                Usuario ash = usuarioRepo.findByEmail("ash@kanto.com").orElse(null);

                if (ash != null) {
                    // --- EQUIPO DE ASH (Full Offensive) ---
                    Equipo teamAsh = new Equipo();
                    teamAsh.setNombre("Equipo de Ensueño");
                    teamAsh.setDescripcion("El equipo campeón de la Liga Mundial.");
                    teamAsh.setFechaCreacion(LocalDateTime.now());
                    teamAsh.setUsuario(ash);
                    teamAsh.setPublico(true);

                    // 1. Pikachu
                    teamAsh.addMiembro(crearMiembro(25L, "pikachu", "Compadre", "Bola Luminosa", "Elec. Estática", "Alegre", 
                            "Rayo", "Cola Férrea", "Voltiocambio", "Ataque Rápido", 0, 252, 0, 0, 4, 252));
                    
                    // 2. Charizard
                    teamAsh.addMiembro(crearMiembro(6L, "charizard", "Dracarys", "Vidasfera", "Poder Solar", "Miedosa", 
                            "Lanzallamas", "Rayo Solar", "Onda Certera", "Respiro", 0, 0, 4, 252, 0, 252));
                    
                    // 3. Greninja
                    teamAsh.addMiembro(crearMiembro(658L, "greninja", "Ninja", "Gafas Elección", "Fuerte Afecto", "Miedosa", 
                            "Shuriken de Agua", "Pulso Umbrío", "Rayo Hielo", "Ida y Vuelta", 0, 0, 4, 252, 0, 252));
                    
                    // 4. Lucario
                    teamAsh.addMiembro(crearMiembro(448L, "lucario", "Aura", "Banda Focus", "Foco Interno", "Alegre", 
                            "A Bocajarro", "Puño Meteoro", "Veloc. Extrema", "Danza Espada", 0, 252, 0, 0, 4, 252));

                    // 5. Gengar
                    teamAsh.addMiembro(crearMiembro(94L, "gengar", "Spooky", "Lodo Negro", "Cuerpo Maldito", "Miedosa", 
                            "Bola Sombra", "Bomba Lodo", "Maquinación", "Sustituto", 0, 0, 4, 252, 0, 252));

                    // 6. Dragonite
                    teamAsh.addMiembro(crearMiembro(149L, "dragonite", "Cartero", "Baya Ziuela", "Foco Interno", "Firme", 
                            "Danza Dragón", "Enfado", "Terremoto", "Veloc. Extrema", 4, 252, 0, 0, 0, 252));

                    equipoRepo.save(teamAsh);
                }

                if (admin != null) {
                    // --- EQUIPO DE ADMIN (Red's Legends - Bulky Offense) ---
                    Equipo teamRed = new Equipo();
                    teamRed.setNombre("Leyendas del Monte Plateado");
                    teamRed.setDescripcion("El equipo definitivo de Rojo.");
                    teamRed.setFechaCreacion(LocalDateTime.now().minusDays(1));
                    teamRed.setUsuario(admin);
                    teamRed.setPublico(true);

                    // 1. Venusaur
                    teamRed.addMiembro(crearMiembro(3L, "venusaur", "Bruteroot", "Lodo Negro", "Clorofila", "Osada", 
                            "Gigadrenado", "Bomba Lodo", "Síntesis", "Somnífero", 252, 0, 252, 4, 0, 0));

                    // 2. Blastoise
                    teamRed.addMiembro(crearMiembro(9L, "blastoise", "Caparazón", "Restos", "Torrente", "Modesta", 
                            "Escaldar", "Rayo Hielo", "Giro Rápido", "Pulso Umbrío", 252, 0, 4, 252, 0, 0));

                    // 3. Snorlax
                    teamRed.addMiembro(crearMiembro(143L, "snorlax", "Grandullón", "Restos", "Sebo", "Cauta", 
                            "Golpe Cuerpo", "Maldición", "Descanso", "Sonámbulo", 252, 0, 4, 0, 252, 0));

                    // 4. Espeon
                    teamRed.addMiembro(crearMiembro(196L, "espeon", "Sol", "Refleluz", "Espejo Mágico", "Miedosa", 
                            "Psíquico", "Brillo Mágico", "Reflejo", "Pantalla de Luz", 0, 0, 4, 252, 0, 252));

                    // 5. Lapras
                    teamRed.addMiembro(crearMiembro(131L, "lapras", "Nessie", "Baya Zidra", "Absorbe Agua", "Modesta", 
                            "Liofilización", "Surf", "Rayo", "Canto Helado", 248, 0, 0, 252, 8, 0));

                    // 6. Pikachu (Red's Ace)
                    teamRed.addMiembro(crearMiembro(25L, "pikachu", "Sparky", "Bola Luminosa", "Elec. Estática", "Ingenua", 
                            "Rayo", "Cola Férrea", "Demolición", "Sorpresa", 0, 252, 0, 4, 0, 252));

                    equipoRepo.save(teamRed);
                    System.out.println(">>> Equipos de prueba creados en Español.");
                }
            }
        };
    }

    private Categoria crearCategoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return c;
    }

    // Método helper para crear miembros de equipo rápido con EVs
    private MiembroEquipo crearMiembro(Long apiId, String nombre, String mote, String objeto, String hab, String nat, 
                                       String m1, String m2, String m3, String m4,
                                       int hp, int atk, int def, int spa, int spd, int spe) {
        MiembroEquipo m = new MiembroEquipo();
        m.setPokemonApiId(apiId);
        m.setNombrePokemon(nombre);
        m.setMote(mote);
        m.setObjeto(objeto);
        m.setHabilidad(hab);
        m.setNaturaleza(nat);
        m.setMovimiento1(m1);
        m.setMovimiento2(m2);
        m.setMovimiento3(m3);
        m.setMovimiento4(m4);
        // EVs
        m.setHpEv(hp);
        m.setAttackEv(atk);
        m.setDefenseEv(def);
        m.setSpAttackEv(spa);
        m.setSpDefenseEv(spd);
        m.setSpeedEv(spe);
        
        return m;
    }
}