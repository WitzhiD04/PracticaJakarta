package dominio;

import infraestructura.ProductoRepository;

import java.util.List;

/**
 * SERVICIO (lógica de negocio)  ·  capa de DOMINIO
 *
 * Aquí van las REGLAS del negocio: qué es un producto válido, qué se puede hacer y qué no.
 * No sabe nada de HTTP ni de HTML (eso es del Servlet y la JSP),
 * ni de SQL (eso es del repositorio). Solo reglas.
 *
 * Es el equivalente a tu AuthService del Taller1.
 * En la diapositiva de la clase, esto es el "Business tier".
 */
public class ProductoService {

    private final ProductoRepository repositorio = new ProductoRepository();

    public List<Producto> listarTodos() {
        return repositorio.listarTodos();
    }

    public List<Producto> buscarPorNombre(String texto) {
        return repositorio.buscarPorNombre(texto.trim());
    }

    public Producto buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    /** Valida las reglas del negocio y, si todo está bien, guarda. */
    public void guardar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (producto.getPrecio() == null || producto.getPrecio().signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        producto.setNombre(producto.getNombre().trim());
        repositorio.guardar(producto);
    }

    public void eliminar(Long id) {
        repositorio.eliminar(id);
    }
}
