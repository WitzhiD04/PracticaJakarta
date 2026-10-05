package infraestructura;

import dominio.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

/**
 * REPOSITORIO (acceso a datos con JPA)  ·  capa de INFRAESTRUCTURA
 *
 * Es lo que en tu Taller1 era "UserRepository extends JpaRepository".
 * Allá Spring te escribía los métodos solo; aquí los escribimos a mano con el EntityManager,
 * que es la herramienta central de JPA:
 *
 *   em.persist(obj)   → INSERT
 *   em.find(...)      → SELECT ... WHERE id = ?
 *   em.merge(obj)     → UPDATE
 *   em.remove(obj)    → DELETE
 *   em.createQuery()  → consultas JPQL (parecen SQL, pero sobre CLASES, no tablas)
 *
 * Patrón que se repite en cada método:
 *   1. pedir un EntityManager
 *   2. usarlo (si cambia datos → dentro de una transacción)
 *   3. cerrarlo SIEMPRE (finally)
 */
public class ProductoRepository {

    /** Todos los productos. JPQL: "FROM Producto" es la CLASE, no la tabla "productos". */
    public List<Producto> listarTodos() {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        try {
            return em.createQuery("SELECT p FROM Producto p ORDER BY p.id", Producto.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /** Búsqueda por nombre. ":texto" es un parámetro: JPA lo rellena de forma segura (evita inyección SQL). */
    public List<Producto> buscarPorNombre(String texto) {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        try {
            return em.createQuery(
                        "SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:texto) ORDER BY p.nombre",
                        Producto.class)
                     .setParameter("texto", "%" + texto + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /** Un producto por su llave primaria. Devuelve null si no existe. */
    public Producto buscarPorId(Long id) {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    /** Cuántos productos hay. JPQL también tiene COUNT, SUM, AVG... */
    public long contar() {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Producto p", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Crear o actualizar.
     * Todo lo que CAMBIA datos va dentro de una transacción:
     * o se guarda completo (commit) o no se guarda nada (rollback).
     * Es lo que hacía @Transactional en tu AuthService.
     */
    public void guardar(Producto producto) {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (producto.getId() == null) {
                em.persist(producto);   // no tiene id → es nuevo → INSERT
            } else {
                em.merge(producto);     // ya tiene id → ya existe → UPDATE
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();          // algo falló: deshacer todo
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Eliminar. Primero hay que buscarlo: remove() solo acepta objetos que el EntityManager conoce. */
    public void eliminar(Long id) {
        EntityManager em = ConfiguracionJpa.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);    // DELETE
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
