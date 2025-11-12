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

    // 1.26.0 -  Updates
    @Operation(summary = "Actualizar solo el nombre de un usuario")
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<Usuario> updateNombre(
            @PathVariable Integer id,
            @RequestParam String nombre) {

        try {
            Usuario actualizado = usuarioService.updateNombre(id, nombre);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar solo el apellido de un usuario")
    @PatchMapping("/{id}/apellido")
    public ResponseEntity<Usuario> updateApellido(
            @PathVariable Integer id,
            @RequestParam String apellido) {

        try {
            Usuario actualizado = usuarioService.updateApellido(id, apellido);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar solo el correo de un usuario")
    @PatchMapping("/{id}/correo")
    public ResponseEntity<Usuario> updateCorreo(
            @PathVariable Integer id,
            @RequestParam String correo) {

        try {
            Usuario actualizado = usuarioService.updateCorreo(id, correo);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar solo la región de un usuario")
    @PatchMapping("/{id}/region")
    public ResponseEntity<Usuario> updateRegion(
            @PathVariable Integer id,
            @RequestParam Integer idRegion) {

        try {
            Usuario actualizado = usuarioService.updateRegion(id, idRegion);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
