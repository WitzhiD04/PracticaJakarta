package dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * ENTIDAD JPA  ·  capa de DOMINIO
 *
 * Es una clase Java normal (un POJO, como dice la diapositiva) con anotaciones.
 * JPA lee esas anotaciones y entiende:
 *   - la clase Producto  = la tabla "productos"
 *   - cada atributo      = una columna
 *   - cada objeto        = una fila
 *
 * Es igual a tu clase User del Taller1: mismas anotaciones, mismo paquete jakarta.persistence.
 */
@Entity                         // "Esta clase se guarda en la base de datos"
@Table(name = "productos")      // ...en la tabla "productos"
public class Producto {

    @Id                                                   // llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // la BD la autoincrementa (1, 2, 3...)
    private Long id;

    @Column(nullable = false, length = 100)               // NOT NULL, VARCHAR(100)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)  // DECIMAL(12,2): plata, sin errores de redondeo
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    /** JPA EXIGE un constructor vacío: lo usa para crear el objeto cuando lee una fila. */
    public Producto() {
    }

    public Producto(String nombre, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y setters: JPA y las JSP (${p.nombre}) los usan para leer y escribir los atributos.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
