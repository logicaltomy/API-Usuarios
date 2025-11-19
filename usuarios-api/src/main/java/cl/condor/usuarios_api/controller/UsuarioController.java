package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.UsuarioDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.findById(id);
            UsuarioDTO usuarioDTO = usuarioService.mapToDTO(usuario);
            return ResponseEntity.ok(usuarioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar usuario por Correo")
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> getByCorreo(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioService.findByCorreo(correo); 
            if (usuario == null) return ResponseEntity.notFound().build();
            
            UsuarioDTO usuarioDTO = usuarioService.mapToDTO(usuario);
            return ResponseEntity.ok(usuarioDTO);
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
            if(e.getMessage().equals("Region no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Login de usuario", description = """
            El usuario puede intentar registrarse, comparamos
            su contrasenia ingresada con el hatch que se encuentra en
            la BD.
            """)
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDTO loginDTO) {
        try {
            usuarioService.login(loginDTO);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            if(e.getMessage().equals("Credenciales invalidas")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
