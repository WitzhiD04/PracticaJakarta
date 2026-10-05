<%--
    VISTA de productos (la "V" de MVC)  ·  capa de PRESENTACIÓN

    Solo PINTA lo que el ProductoServlet le dejó en el request. No busca datos ni tiene lógica.

    Dos cosas nuevas respecto al index.jsp:
      ${...}       = "Expression Language" (EL). ${productos} lee lo que el Servlet guardó con
                     request.setAttribute("productos", ...). ${p.nombre} llama a p.getNombre().
      <c:forEach>  = etiquetas JSTL. Hacen ciclos e ifs sin meter código Java en el HTML.
      <c:out>      = imprime un texto de forma segura (si alguien escribe <script> en un nombre, no se ejecuta).
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_CO"/>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Productos · Ejemplo JPA</title>
    <style>
        * { box-sizing: border-box; }
        body { font-family: system-ui, Segoe UI, sans-serif; background: #f4f5f7; color: #1d2433; margin: 0; padding: 24px 16px; }
        .contenedor { max-width: 920px; margin: 0 auto; display: grid; gap: 20px; }
        .tarjeta { background: #fff; border: 1px solid #dde1e8; border-radius: 10px; padding: 20px; }
        h1 { margin: 0 0 4px; font-size: 28px; }
        h2 { margin: 0 0 14px; font-size: 19px; }
        .sub { color: #5b6475; margin: 0; }
        .volver { color: #3056d3; text-decoration: none; font-size: 14px; }
        .aviso { padding: 10px 14px; border-radius: 8px; }
        .ok { background: #e3f4ea; border: 1px solid #9ad3b0; }
        .error { background: #fde8e6; border: 1px solid #f0a59c; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 10px 8px; border-bottom: 1px solid #e6e9ef; }
        th { font-size: 12px; text-transform: uppercase; letter-spacing: .05em; color: #5b6475; }
        td.num { text-align: right; font-variant-numeric: tabular-nums; }
        .acciones { display: flex; gap: 8px; justify-content: flex-end; }
        form.linea { display: flex; gap: 8px; flex-wrap: wrap; }
        input { padding: 9px 10px; border: 1px solid #c8ced9; border-radius: 6px; font: inherit; }
        input[name=buscar] { flex: 1; min-width: 180px; }
        .campos { display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 12px; }
        .campos label { display: grid; gap: 4px; font-size: 14px; color: #5b6475; }
        button, .boton { padding: 9px 14px; border-radius: 6px; border: 1px solid #3056d3; background: #3056d3;
                         color: #fff; font: inherit; cursor: pointer; text-decoration: none; display: inline-block; }
        .secundario { background: #fff; color: #3056d3; }
        .peligro { background: #fff; color: #c0392b; border-color: #c0392b; }
        .pie-form { display: flex; gap: 8px; margin-top: 14px; }
        .vacio { color: #5b6475; text-align: center; padding: 20px; }
        @media (max-width: 640px) { .campos { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="contenedor">

    <header>
        <a class="volver" href="${base}/">← Inicio</a>
        <h1>Productos</h1>
        <p class="sub">CRUD de ejemplo con Servlet + JSP + JPA (Hibernate) + H2</p>
    </header>

    <%-- Mensajes: ?mensaje=... viene del redirect del Servlet; ${error} de una validación fallida --%>
    <c:if test="${param.mensaje == 'guardado'}"><div class="aviso ok">Producto guardado.</div></c:if>
    <c:if test="${param.mensaje == 'eliminado'}"><div class="aviso ok">Producto eliminado.</div></c:if>
    <c:if test="${not empty error}"><div class="aviso error"><c:out value="${error}"/></div></c:if>

    <%-- ===== Formulario: crear o editar (es el mismo; si trae id, es edición) ===== --%>
    <section class="tarjeta">
        <c:choose>
            <c:when test="${empty productoEnEdicion.id}"><h2>Nuevo producto</h2></c:when>
            <c:otherwise><h2>Editar producto #${productoEnEdicion.id}</h2></c:otherwise>
        </c:choose>
        <form method="post" action="${base}/productos">
            <input type="hidden" name="accion" value="guardar">
            <input type="hidden" name="id" value="${productoEnEdicion.id}">
            <div class="campos">
                <label>Nombre
                    <input name="nombre" maxlength="100" required value="<c:out value='${productoEnEdicion.nombre}'/>">
                </label>
                <label>Precio
                    <input name="precio" inputmode="decimal" required value="${productoEnEdicion.precio}">
                </label>
                <label>Stock
                    <input name="stock" type="number" min="0" required value="${productoEnEdicion.stock}">
                </label>
            </div>
            <div class="pie-form">
                <button type="submit">Guardar</button>
                <c:if test="${not empty productoEnEdicion}">
                    <a class="boton secundario" href="${base}/productos">Cancelar</a>
                </c:if>
            </div>
        </form>
    </section>

    <%-- ===== Lista ===== --%>
    <section class="tarjeta">
        <h2>Lista</h2>

        <%-- Búsqueda: es un GET, así que la búsqueda queda en la URL (?buscar=...) --%>
        <form class="linea" method="get" action="${base}/productos" style="margin-bottom:14px">
            <input name="buscar" placeholder="Buscar por nombre…" value="<c:out value='${param.buscar}'/>">
            <button type="submit" class="secundario">Buscar</button>
            <c:if test="${not empty param.buscar}">
                <a class="boton secundario" href="${base}/productos">Ver todos</a>
            </c:if>
        </form>

        <table>
            <thead>
            <tr><th>ID</th><th>Nombre</th><th style="text-align:right">Precio</th><th style="text-align:right">Stock</th><th></th></tr>
            </thead>
            <tbody>
            <%-- c:forEach = un for-each de Java: por cada Producto p de la lista... --%>
            <c:forEach var="p" items="${productos}">
                <tr>
                    <td>${p.id}</td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td class="num">$ <fmt:formatNumber value="${p.precio}" maxFractionDigits="2"/></td>
                    <td class="num">${p.stock}</td>
                    <td>
                        <div class="acciones">
                            <a class="boton secundario" href="${base}/productos?accion=editar&id=${p.id}">Editar</a>
                            <%-- Eliminar va por POST (no por un link), porque cambia datos --%>
                            <form method="post" action="${base}/productos"
                                  onsubmit="return confirm('¿Eliminar este producto?')">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${p.id}">
                                <button type="submit" class="peligro">Eliminar</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty productos}">
                <tr><td colspan="5" class="vacio">No hay productos.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>

</div>
</body>
</html>
