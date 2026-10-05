package infraestructura;

import dominio.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.math.BigDecimal;

/**
 * ARRANQUE Y APAGADO DE JPA  ·  capa de INFRAESTRUCTURA
 *
 * @WebListener = Tomcat avisa a esta clase cuando la app arranca y cuando se apaga.
 * Es el mismo ciclo de vida del Servlet (init / destroy), pero para la app completa.
 *
 * Aquí se crea UNA sola EntityManagerFactory para toda la app:
 *   - EntityManagerFactory = la "fábrica". Es pesada (lee persistence.xml, conecta a la BD),
 *     así que se crea una sola vez.
 *   - EntityManager = el "trabajador" que hace persist, find, remove... Es liviano:
 *     se crea uno por cada operación y se cierra al terminar.
 *
 * En Spring Boot esto pasaba solo y nunca lo veías.
 */
@WebListener
public class ConfiguracionJpa implements ServletContextListener {

    private static EntityManagerFactory fabrica;

    /** Tomcat lo llama UNA vez, cuando arranca la app. */
    @Override
    public void contextInitialized(ServletContextEvent evento) {
        // Busca en persistence.xml la unidad llamada "tiendaPU"
        fabrica = Persistence.createEntityManagerFactory("tiendaPU");
        cargarDatosDeEjemplo();
    }

    /** Tomcat lo llama UNA vez, cuando se apaga la app. */
    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        if (fabrica != null) {
            fabrica.close();
        }
    }

    /** Los repositorios piden aquí un EntityManager nuevo para cada operación. */
    public static EntityManager crearEntityManager() {
        return fabrica.createEntityManager();
    }

    /** Mete unos productos para que la página no arranque vacía. */
    private void cargarDatosDeEjemplo() {
        ProductoRepository repositorio = new ProductoRepository();
        if (repositorio.contar() == 0) {
            repositorio.guardar(new Producto("Teclado mecánico", new BigDecimal("189900"), 12));
            repositorio.guardar(new Producto("Mouse inalámbrico", new BigDecimal("65000"), 30));
            repositorio.guardar(new Producto("Monitor 24 pulgadas", new BigDecimal("749000"), 5));
        }
    }
}
