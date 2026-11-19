package CampusLands.logitrack.auth;

import CampusLands.logitrack.dto.response.UsuarioDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.mapper.UsuarioMapper;
import CampusLands.logitrack.model.EstadoUsuario;
import CampusLands.logitrack.model.Rol;
import CampusLands.logitrack.model.Usuario;
import CampusLands.logitrack.repository.UsuarioRepository;
import CampusLands.logitrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        // Busco el usuario en la base de datos
        List<Usuario> usuarios = usuarioRepository.findByUsername(request.username());

        if (usuarios.isEmpty() || !usuarios.get(0).getPassword().equals(request.password())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        Usuario usuario = usuarios.get(0);

        // Solo los usuarios aprobados pueden entrar
        if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
            throw new BusinessRuleException("Tu solicitud de registro esta pendiente de aprobacion");
        }
        if (usuario.getEstado() == EstadoUsuario.RECHAZADO) {
            throw new BusinessRuleException("Tu solicitud de registro fue rechazada");
        }

        // El token SOLO lleva el username. El rol se consulta en la BD en cada peticion.
        String token = jwtService.generateToken(usuario.getUsername());

        return Map.of("token", token,
                "username", usuario.getUsername(),
                "rol", usuario.getRol().name());
    }

    /*
     * Registro publico.
     * Todo el que se registra entra como EMPLEADO y en estado PENDIENTE.
     * No se pueden crear mas administradores desde aqui.
     */
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest request) {

        // No permito usernames repetidos
        List<Usuario> existentes = usuarioRepository.findByUsername(request.username());
        if (!existentes.isEmpty()) {
            throw new BusinessRuleException("El username ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(request.password());
        usuario.setRol(Rol.EMPLEADO);
        usuario.setEstado(EstadoUsuario.PENDIENTE);
        usuarioRepository.save(usuario);

        return Map.of("mensaje", "Solicitud enviada. Espera la aprobacion del administrador");
    }

    // ================= SOLO ADMIN =================

    // Lista las solicitudes de registro que estan esperando aprobacion
    @GetMapping("/pendientes")
    public List<UsuarioDtoResponse> pendientes() {
        return usuarioRepository.findByEstado(EstadoUsuario.PENDIENTE).stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    // Aprueba una solicitud: el usuario pasa a ACTIVO y ya puede iniciar sesion
    @PostMapping("/aprobar/{id}")
    public UsuarioDtoResponse aprobar(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe esa solicitud de registro"));

        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    // Rechaza una solicitud: el usuario queda RECHAZADO y no puede iniciar sesion
    @PostMapping("/rechazar/{id}")
    public UsuarioDtoResponse rechazar(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe esa solicitud de registro"));

        usuario.setEstado(EstadoUsuario.RECHAZADO);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }
}
