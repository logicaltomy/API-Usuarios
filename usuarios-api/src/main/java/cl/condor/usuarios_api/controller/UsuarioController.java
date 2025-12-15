package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.PreguntasResponseDTO;
import cl.condor.usuarios_api.dto.RecuperacionDTO;
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

@Tag(
        name = "Usuarios",
        description = """
            Controlador principal del microservicio de Usuarios.
            Gestiona la creación, consulta, listado y seguridad (login/recuperación).
            """
)
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ==================================================================
    //  ENDPOINTS EXISTENTES (GET, POST, PATCH) - SE MANTIENEN IGUAL
    // ==================================================================

    @Operation(
        summary = "Listar todos los usuarios",
        description = "Retorna la lista completa de usuarios registrados en el sistema.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No hay usuarios registrados.")
        }
    )
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> lista = usuarioService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
        summary = "Buscar usuario por ID",
        description = "Devuelve un usuario específico según su identificador. Si no existe, responde con HTTP 404 Not Found.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
        }
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

    @Operation(
        summary = "Buscar usuario por Correo",
        description = "Busca un usuario por su correo electrónico. Si no existe, responde con HTTP 404 Not Found.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
        }
    )
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
        summary = "Crear un nuevo usuario (Registro)",
        description = "Registra un nuevo usuario. Requiere nombre, correo, contraseña, región, rol y las 2 preguntas y respuestas de seguridad.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON con los datos del usuario a registrar, incluyendo preguntas y respuestas de seguridad.",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\n  \"nombre\": \"Juan Perez\",\n  \"correo\": \"juan@mail.com\",\n  \"password\": \"123456\",\n  \"region\": 1,\n  \"rol\": 2,\n  \"pregunta1\": \"¿Nombre de tu mascota?\",\n  \"respuesta1\": \"Firulais\",\n  \"pregunta2\": \"¿Ciudad de nacimiento?\",\n  \"respuesta2\": \"Santiago\"\n}"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado exitosamente."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes.")
        }
    )
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            Usuario saved = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            // Capturamos errores de validación (ej: faltan preguntas de seguridad)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Actualizar solo el nombre de un usuario",
        description = "Actualiza el campo 'nombre' de un usuario específico por su ID.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nombre actualizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
        }
    )
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<Usuario> updateNombre(@PathVariable Integer id, @RequestParam String nombre) {
        try {
            Usuario actualizado = usuarioService.updateNombre(id, nombre);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Actualizar solo el correo de un usuario",
        description = "Actualiza el campo 'correo' de un usuario específico por su ID.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Correo actualizado."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
        }
    )
    @PatchMapping("/{id}/correo")
    public ResponseEntity<Usuario> updateCorreo(@PathVariable Integer id, @RequestParam String correo) {
        try {
            Usuario actualizado = usuarioService.updateCorreo(id, correo);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Actualizar solo la región de un usuario",
        description = "Actualiza el campo 'region' de un usuario específico por su ID.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Región actualizada."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario o región no encontrada.")
        }
    )
    @PatchMapping("/{id}/region")
    public ResponseEntity<Usuario> updateRegion(@PathVariable Integer id, @RequestParam Integer idRegion) {
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

    @Operation(
        summary = "Actualizar rutas recorridas",
        description = "Actualiza el número de rutas recorridas por el usuario.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rutas recorridas actualizadas."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
        }
    )
    @PatchMapping("/{id}/rutasRecorridas")
    public ResponseEntity<Usuario> updateRutasRecorridas(@PathVariable Integer id, @RequestParam Integer nuevasRutas) {
        try {
            Usuario actualizado = usuarioService.updateRutasRecorridas(id, nuevasRutas);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Login de usuario",
        description = "Verifica las credenciales del usuario (correo y contraseña).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON con correo y contraseña.",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\n  \"correo\": \"juan@mail.com\",\n  \"password\": \"123456\"\n}"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor.")
        }
    )
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

    @Operation(
        summary = "Actualizar foto de perfil (Base64)",
        description = "Actualiza la foto de perfil del usuario en formato Base64.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Cadena Base64 de la imagen.",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "text/plain",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "iVBORw0KGgoAAAANSUhEUgAA... (Base64)"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Foto actualizada."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error al actualizar la foto.")
        }
    )
    @PatchMapping("/{id}/foto")
    public ResponseEntity<Usuario> updateFotoPerfil(@PathVariable Integer id, @RequestBody String fotoBase64) {
        try {
            Usuario actualizado = usuarioService.updateFoto(id, fotoBase64);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    // ==================================================================
    //  NUEVOS ENDPOINTS PARA RECUPERACIÓN DE CONTRASEÑA
    // ==================================================================

    @Operation(
        summary = "Obtener preguntas de seguridad",
        description = "Dado un correo, devuelve las 2 preguntas que el usuario configuró para poder mostrarlas en el Frontend.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preguntas de seguridad encontradas.",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        value = "{\n  \"pregunta1\": \"¿Nombre de tu mascota?\",\n  \"pregunta2\": \"¿Ciudad de nacimiento?\"\n}"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado o sin preguntas configuradas.")
        }
    )
    @GetMapping("/preguntas")
    public ResponseEntity<?> obtenerPreguntas(@RequestParam String correo) {
        try {
            PreguntasResponseDTO response = usuarioService.obtenerPreguntasSeguridad(correo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retorna 404 si el usuario no existe o 400 si no tiene preguntas configuradas
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Recuperar Contraseña",
        description = "Recibe correo, las 2 respuestas de seguridad y la nueva contraseña. Si las respuestas coinciden (case-insensitive), actualiza la password.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON con correo, respuestas y nueva contraseña.",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\n  \"correo\": \"juan@mail.com\",\n  \"respuesta1\": \"Firulais\",\n  \"respuesta2\": \"Santiago\",\n  \"nuevaPassword\": \"nueva123\"\n}"
                )
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña restablecida con éxito."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Respuestas incorrectas o datos inválidos.")
        }
    )
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarContrasena(@RequestBody RecuperacionDTO recuperacionDTO) {
        try {
            usuarioService.recuperarContrasena(recuperacionDTO);
            // Devolvemos un mensaje simple o un JSON 200 OK
            return ResponseEntity.ok("Contraseña restablecida con éxito.");
        } catch (RuntimeException e) {
            // Si las respuestas son incorrectas, devolvemos 400 Bad Request con el mensaje del error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
