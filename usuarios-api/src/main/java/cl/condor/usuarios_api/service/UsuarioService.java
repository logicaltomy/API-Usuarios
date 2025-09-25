package cl.condor.usuarios_api.service;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.repository.EstadoRepository;
import cl.condor.usuarios_api.repository.RegionRepository;
import cl.condor.usuarios_api.repository.RolRepository;
import cl.condor.usuarios_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Usuario save(Usuario usuario) {
        if (usuario.getIdRegion() == null || !regionRepository.existsById(usuario.getIdRegion())) {
            throw new RuntimeException("La región es obligatoria, no se puede guardar el Usuario");
        }
        if (usuario.getIdEstado() == null || !estadoRepository.existsById(usuario.getIdEstado())) {
            throw new RuntimeException("Estado no encontrado, no se puede guardar el Usuario");
        }
        if (usuario.getIdRol() == null || !rolRepository.existsById(usuario.getIdRol())) {
            throw new RuntimeException("Rol no encontrado, no se puede guardar el Usuario");
        }
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

}
