package CampusLands.logitrack.config;

import CampusLands.logitrack.model.Auditoria;
import CampusLands.logitrack.model.TipoOperacion;
import CampusLands.logitrack.repository.AuditoriaRepository;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/*
 * Listener de JPA: se ejecuta solo cada vez que se inserta, actualiza o elimina
 * una entidad que tenga @EntityListeners(AuditoriaListener.class).
 *
 * Asi la auditoria se registra automaticamente, sin que el service tenga que acordarse.
 *
 * OJO: esta clase NO es un @Component. La crea Hibernate, no Spring,
 * por eso las dependencias se piden al contexto con ApplicationContextProvider.
 */
public class AuditoriaListener {

    // Guarda el estado anterior de cada entidad, capturado cuando se carga de la BD
    private static final Map<Object, String> ANTERIORES = Collections.synchronizedMap(new WeakHashMap<>());

    // Cuando la entidad se lee de la base, guardo una foto de como estaba
    @PostLoad
    public void alCargar(Object entidad) {
        ANTERIORES.put(entidad, describir(entidad));
    }

    // INSERT
    @PostPersist
    public void alInsertar(Object entidad) {
        registrar(TipoOperacion.INSERT, entidad, null, describir(entidad));
    }

    // UPDATE
    @PostUpdate
    public void alActualizar(Object entidad) {
        registrar(TipoOperacion.UPDATE, entidad, ANTERIORES.get(entidad), describir(entidad));
    }

    // DELETE
    @PostRemove
    public void alEliminar(Object entidad) {
        registrar(TipoOperacion.DELETE, entidad, describir(entidad), null);
    }

    private void registrar(TipoOperacion tipo, Object entidad, String anteriores, String nuevos) {

        // La auditoria no se audita a si misma
        if (entidad instanceof Auditoria) return;

        Auditoria auditoria = new Auditoria();
        auditoria.setTipoOperacion(tipo);
        auditoria.setFechaHora(LocalDateTime.now());
        auditoria.setUsuario(usuarioActual());
        auditoria.setEntidadAfectada(entidad.getClass().getSimpleName());
        auditoria.setValoresAnteriores(anteriores);
        auditoria.setValoresNuevos(nuevos);

        // Le pido el repositorio al contexto de Spring
        if (ApplicationContextProvider.getContext() == null) return;
        AuditoriaRepository repositorio = ApplicationContextProvider.getContext().getBean(AuditoriaRepository.class);
        repositorio.save(auditoria);
    }

    // El usuario sale del token JWT que Spring guardo en el contexto
    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return "sistema";
        }
        return auth.getName();
    }

    /*
     * Convierto la entidad a texto: {campo=valor, campo=valor}.
     * Solo tomo campos simples (texto, numeros, fechas, enums).
     * Las relaciones (@ManyToOne) se saltan para no disparar consultas
     * extra ni ciclos infinitos.
     */
    private String describir(Object entidad) {

        StringBuilder texto = new StringBuilder("{");
        Field[] campos = entidad.getClass().getDeclaredFields();
        boolean primero = true;

        for (Field campo : campos) {
            campo.setAccessible(true);
            try {
                Object valor = campo.get(entidad);
                if (valor != null && esSimple(valor)) {
                    if (!primero) texto.append(", ");
                    texto.append(campo.getName()).append("=").append(valor);
                    primero = false;
                }
            } catch (Exception e) {
                // si un campo no se puede leer, se ignora
            }
        }

        texto.append("}");
        return texto.toString();
    }

    private boolean esSimple(Object valor) {
        if (valor instanceof Collection) return false;
        return valor instanceof String
                || valor instanceof Number
                || valor instanceof Boolean
                || valor instanceof BigDecimal
                || valor instanceof Temporal
                || valor instanceof Enum;
    }
}
