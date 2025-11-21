package cl.condor.usuarios_api.service;
import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.UsuarioDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.repository.EstadoRepository;
import cl.condor.usuarios_api.repository.RegionRepository;
import cl.condor.usuarios_api.repository.RolRepository;
import cl.condor.usuarios_api.repository.UsuarioRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RolRepository rolRepository;

    // acá tenemos el bean
    private final PasswordEncoder encoder;

    public UsuarioDTO mapToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .fotoPerfil(usuario.getFotoPerfil())
                .rutasRecorridas(usuario.getRutasRecorridas())
                .kmRecorridos(usuario.getKmRecorridos())
                .idRol(usuario.getIdRol())
                .idRegion(usuario.getIdRegion())
                .idEstado(usuario.getIdEstado())
                .build();
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Usuario depende de 3 tablas para su creación
    // Depende de rol, estado, region
    // hacer region como null?
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario.getIdRegion() == null || !regionRepository.existsById(usuario.getIdRegion())) {
            throw new RuntimeException("La región es obligatoria, no se puede guardar el Usuario");
        }
        if (usuario.getIdRol() == null || !rolRepository.existsById(usuario.getIdRol())) {
            throw new RuntimeException("Rol no encontrado, no se puede guardar el Usuario");
        }
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    // 1.26.0 -  Updates

    @Transactional
    public Usuario updateNombre(Integer id, String nuevoNombre) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombre(nuevoNombre);
        return usuarioRepository.save(usuario);
    }


    @Transactional
    public Usuario updateCorreo(Integer id, String nuevoCorreo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setCorreo(nuevoCorreo);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateRegion(Integer id, Integer nuevaRegion) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setIdRegion(nuevaRegion);
        return usuarioRepository.save(usuario);
    }

    public void login(LoginDTO loginDTO) {
        // CAMBIO: Usamos findByCorreo y obtenemos el correo del DTO
        Usuario usuario = usuarioRepository.findByCorreo(loginDTO.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        // Esta parte se mantiene igual (verificación de contraseña)
        if (!encoder.matches(loginDTO.getPassword(), usuario.getContrasena())){
            throw new RuntimeException("Credenciales invalidas");
        }
    }
    
    // Este método es necesario para que funcione el controller "/buscar"
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }
    // Actualizar foto de perfil
    
    @Transactional
    public Usuario updateFoto(Integer id, String fotoBase64) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validación básica para evitar null pointers
        if (fotoBase64 == null || fotoBase64.trim().isEmpty()) {
            throw new RuntimeException("La foto no puede estar vacía");
        }

        try {
            // LIMPIEZA: A veces el Base64 viene con prefijo "data:image/jpeg;base64," 
            // o saltos de línea que rompen el decodificador.
            String cleanBase64 = fotoBase64;
            
            if (fotoBase64.contains(",")) {
                // Tomamos solo la parte después de la coma
                cleanBase64 = fotoBase64.split(",")[1]; 
            }
            
            // Eliminar espacios en blanco o saltos de linea accidentales
            cleanBase64 = cleanBase64.replaceAll("\\s", "");

            // Decodificar String Base64 -> byte[]
            byte[] fotoBytes = java.util.Base64.getDecoder().decode(cleanBase64);
            
            usuario.setFotoPerfil(fotoBytes);
            return usuarioRepository.save(usuario);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El string enviado no es un Base64 válido: " + e.getMessage());
        }
    }

}
