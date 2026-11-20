package CampusLands.logitrack.auth;

import CampusLands.logitrack.dto.response.UsuarioDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.mapper.UsuarioMapper;
import CampusLands.logitrack.model.EstadoUsuario;
import CampusLands.logitrack.model.Rol;
import CampusLands.logitrack.model.Usuario;
import CampusLands.logitrack.repository.UsuarioRepository;
import CampusLands.logitrack.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {

        // Busco el usuario en la base de datos
        List<Usuario> usuarios = usuarioRepository.findByUsername(request.username());

        if (usuarios.isEmpty() || !usuarios.get(0).getPassword().equals(request.password())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        Usuario usuario = usuarios.get(0);

        // Solo los usuarios aprobados pueden entrar
        if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
            throw new BusinessRuleException("Su solicitud de registro está pendiente de aprobación");
        }
        if (usuario.getEstado() == EstadoUsuario.RECHAZADO) {
            throw new BusinessRuleException("Su solicitud de registro fue rechazada");
        }
        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new BusinessRuleException("Su cuenta fue dada de baja. Consulte con el administrador");
        }

        // El token SOLO lleva el username. El rol se consulta en la BD en cada peticion.
        String token = jwtService.generateToken(usuario.getUsername());

        return Map.of("token", token,
                "username", usuario.getUsername(),
                "nombre", usuario.getNombre(),
                "rol", usuario.getRol().name());
    }

    /*
     * Registro publico.
     * Todo el que se registra entra como EMPLEADO y en estado PENDIENTE.
     * No se pueden crear mas administradores desde aqui.
     */
    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {

        // No permito usernames repetidos
        List<Usuario> existentes = usuarioRepository.findByUsername(request.username());
        if (!existentes.isEmpty()) {
            throw new BusinessRuleException("El username ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(request.password());
        usuario.setNombre(request.nombre());
        usuario.setTelefono(request.telefono());
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

    // ================= GESTION DE USUARIOS =================

    // Lista todos los usuarios del sistema, sea cual sea su estado
    @GetMapping("/usuarios")
    public List<UsuarioDtoResponse> usuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    /*
     * Da de baja a un usuario.
     * No se borra la fila: pasa a INACTIVO y no puede iniciar sesion.
     * Se hace asi porque sus movimientos y su rastro en la auditoria
     * tienen que seguir existiendo (no se puede borrar a alguien que ya opero).
     */
    @PostMapping("/usuarios/{id}/baja")
    public UsuarioDtoResponse darDeBaja(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe ese usuario"));

        // Un administrador no puede darse de baja a si mismo
        if (usuario.getUsername().equals(usuarioActual())) {
            throw new BusinessRuleException("No puede darse de baja a usted mismo");
        }

        usuario.setEstado(EstadoUsuario.INACTIVO);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    // Vuelve a habilitar a un usuario que estaba dado de baja
    @PostMapping("/usuarios/{id}/alta")
    public UsuarioDtoResponse darDeAlta(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe ese usuario"));

        if (usuario.getEstado() == EstadoUsuario.PENDIENTE) {
            throw new BusinessRuleException("Ese usuario está pendiente: apruébelo desde Solicitudes");
        }

        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    // Convierte a un usuario en administrador
    @PostMapping("/usuarios/{id}/admin")
    public UsuarioDtoResponse hacerAdministrador(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe ese usuario"));

        if (usuario.getRol() == Rol.ADMIN) {
            throw new BusinessRuleException("Ese usuario ya es administrador");
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BusinessRuleException("Solo se puede hacer administrador a un usuario activo");
        }

        usuario.setRol(Rol.ADMIN);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    // Quita el rol de administrador y lo deja como empleado
    @PostMapping("/usuarios/{id}/empleado")
    public UsuarioDtoResponse quitarAdministrador(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe ese usuario"));

        if (usuario.getUsername().equals(usuarioActual())) {
            throw new BusinessRuleException("No puede quitarse a usted mismo el rol de administrador");
        }

        usuario.setRol(Rol.EMPLEADO);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    // Username del usuario autenticado que esta haciendo la peticion
    private String usuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
