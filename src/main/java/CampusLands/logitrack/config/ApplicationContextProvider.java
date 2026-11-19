package CampusLands.logitrack.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*
 * Guarda el ApplicationContext de Spring en una variable estatica.
 *
 * ¿Para que? Porque los listeners de JPA (@EntityListeners) los crea Hibernate,
 * no Spring. Entonces no se les puede inyectar nada con @Autowired ni @PersistenceContext.
 * Con esto, el listener puede pedirle los beans al contexto cuando los necesita.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
