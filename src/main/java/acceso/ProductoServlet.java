package acceso;

import dominio.Producto;
import dominio.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * SERVLET = el CONTROLADOR (la "C" de MVC)  ·  capa de ACCESO
 *
 * Es la puerta de entrada: recibe las peticiones HTTP de /productos.
 * NO escribe HTML (ya no hay out.println): busca los datos y se los pasa a la JSP.
 *
 *   GET  /productos                       → mostrar la lista
 *   GET  /productos?buscar=mouse          → mostrar solo los que coinciden
 *   GET  /productos?accion=editar&id=2    → mostrar la lista + el formulario lleno
 *   POST /productos  (accion=guardar)     → crear o actualizar
 *   POST /productos  (accion=eliminar)    → borrar
 *
 * Es el equivalente a tu AuthController del Taller1.
 */
@WebServlet(name = "productoServlet", value = "/productos")
public class ProductoServlet extends HttpServlet {

    /** La vista va dentro de WEB-INF: así nadie puede abrirla directo, solo a través de este Servlet. */
    private static final String VISTA = "/WEB-INF/vistas/productos.jsp";

    private ProductoService servicio;

    @Override
    public void init() {
        servicio = new ProductoService();
    }

    /** GET = el usuario quiere VER algo. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("editar".equals(request.getParameter("accion"))) {
            Long id = Long.valueOf(request.getParameter("id"));
            request.setAttribute("productoEnEdicion", servicio.buscarPorId(id));
        }
        mostrarVista(request, response);
    }

    /** POST = el usuario quiere CAMBIAR algo (viene de un formulario). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");   // para que las tildes y la ñ lleguen bien

        if ("eliminar".equals(request.getParameter("accion"))) {
            servicio.eliminar(Long.valueOf(request.getParameter("id")));
            // Después de cambiar algo, se REDIRIGE en vez de mostrar la vista directo.
            // Así, si el usuario recarga la página (F5), no se repite la operación.
            response.sendRedirect(request.getContextPath() + "/productos?mensaje=eliminado");
            return;
        }

        // accion = guardar: armamos el objeto con lo que llegó del formulario
        Producto producto = new Producto();
        String id = request.getParameter("id");
        if (id != null && !id.isBlank()) {
            producto.setId(Long.valueOf(id));   // trae id → es una edición
        }
        producto.setNombre(request.getParameter("nombre"));

        try {
            producto.setPrecio(new BigDecimal(textoONada(request.getParameter("precio")).replace(",", ".")));
            producto.setStock(Integer.valueOf(textoONada(request.getParameter("stock"))));
            servicio.guardar(producto);
            response.sendRedirect(request.getContextPath() + "/productos?mensaje=guardado");

        } catch (NumberFormatException e) {
            mostrarError(request, response, producto, "El precio y el stock deben ser números.");
        } catch (IllegalArgumentException e) {
            // Una regla del ProductoService no se cumplió: mostramos su mensaje
            mostrarError(request, response, producto, e.getMessage());
        }
    }

    /** Busca la lista de productos y le pasa todo a la JSP para que la pinte. */
    private void mostrarVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String buscar = request.getParameter("buscar");
        List<Producto> productos = (buscar == null || buscar.isBlank())
                ? servicio.listarTodos()
                : servicio.buscarPorNombre(buscar);

        request.setAttribute("productos", productos);              // en la JSP: ${productos}
        request.getRequestDispatcher(VISTA).forward(request, response);  // "píntalo con esta vista"
    }

    /** Vuelve a mostrar el formulario con lo que el usuario escribió + el mensaje de error. */
    private void mostrarError(HttpServletRequest request, HttpServletResponse response,
                              Producto producto, String mensaje) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.setAttribute("productoEnEdicion", producto);
        mostrarVista(request, response);
    }

    private String textoONada(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
