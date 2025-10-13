package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Usuarios",
        description = """
            Controlador principal del microservicio de Usuarios.
            Gestiona la creación, consulta y listado de los usuarios registrados.
            Cada usuario está asociado a un Rol, una Región y un Estado.
            Las contraseñas se almacenan de forma segura mediante hash (BCrypt).
            """
)
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
            summary = "Listar todos los usuarios",
            description = """
                Retorna la lista completa de usuarios registrados.
                Si no hay registros, devuelve HTTP 204 No Content.
                """
    )
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> lista = usuarioService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = """
                Devuelve un usuario específico según su identificador.
                Si no existe, responde con HTTP 404 Not Found.
                """
    )
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear un nuevo usuario",
            description = """
                Registra un nuevo usuario en el sistema.
                La contraseña se encripta automáticamente con BCrypt 
                antes de guardarse en la base de datos.
                """
    )
    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.status(201).body(saved);
    }
}
