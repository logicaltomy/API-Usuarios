package cl.condor.usuarios_api.config;

import cl.condor.usuarios_api.model.*;
import cl.condor.usuarios_api.repository.*;
import cl.condor.usuarios_api.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EstadoRepository estadoRepository;
    private final RegionRepository regionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) {
        // Evita duplicar si ya hay data
        // if (usuarioRepository.count() > 0) return;

        // Crear tablas maestras si están vacías
        Rol rol = rolRepository.findAll().stream().findFirst()
                .orElseGet(() -> rolRepository.save(new Rol(/* setNombre("USUARIO") etc */)));

        Estado estado = estadoRepository.findAll().stream().findFirst()
                .orElseGet(() -> estadoRepository.save(new Estado(/* setNombre("Activo") etc */)));

        Region region = regionRepository.findAll().stream().findFirst()
                .orElseGet(() -> regionRepository.save(new Region(/* setNombre("Metropolitana") etc */)));

        // Crear usuarios
        Faker faker = new Faker(new Locale("es-CL"), new Random(42));

        for (int i = 0; i < 20; i++) {
            String nombre = faker.name().firstName();
            String apellido = faker.name().lastName();
            String correo = (nombre + "." + apellido + i + "@example.com").toLowerCase();

            Usuario u = Usuario.builder()
                    .nombre(nombre)
                    .apellido(apellido)
                    .correo(correo)
                    .fNacimiento(randomDateBetween(
                            LocalDate.of(1970, 1, 1),
                            LocalDate.of(2006, 12, 31),
                            i))
                    .contrasena("Secret123!")
                    .token(null)
                    .fotoPerfil(null)
                    .rutasRecorridas(faker.number().numberBetween(0, 100))
                    .kmRecorridos(BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000)))
                    .idRol(rol.getId())
                    .idRegion(region.getId())
                    .idEstado(estado.getId())
                    .build();

            usuarioService.save(u); // usa encoder + validaciones
        }
    }

    private LocalDate randomDateBetween(LocalDate start, LocalDate end, int salt) {
        long startEpoch = start.toEpochDay();
        long endEpoch = end.toEpochDay();
        long days = endEpoch - startEpoch;
        Random r = new Random(42L + salt);
        long randomDay = startEpoch + (Math.abs(r.nextLong()) % (days + 1));
        return LocalDate.ofEpochDay(randomDay);
    }
}
