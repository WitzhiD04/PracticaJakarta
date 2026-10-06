<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Ejemplo Jakarta EE</title>
    <style>
        body { font-family: system-ui, Segoe UI, sans-serif; background: #f4f5f7; color: #1d2433; margin: 0; padding: 40px 16px; }
        main { max-width: 640px; margin: 0 auto; display: grid; gap: 16px; }
        a.tarjeta { display: block; background: #fff; border: 1px solid #dde1e8; border-radius: 10px; padding: 18px 20px;
                    color: inherit; text-decoration: none; }
        a.tarjeta:hover { border-color: #3056d3; }
        a.tarjeta b { color: #3056d3; font-size: 18px; }
        p { margin: 4px 0 0; color: #5b6475; }
    </style>
</head>
<body>
<main>
    <h1><%= "Bienvenido mi rey" %></h1>

    <a class="tarjeta" href="hello-servlet">
        <b>Hello Servlet →</b>
        <p>Forma 2: el Servlet arma el HTML él solo con out.println.</p>
    </a>

    <a class="tarjeta" href="productos">
        <b>CRUD de productos (JPA) →</b>
        <p>Forma 3 (MVC): Servlet + JSP + JPA. Crear, listar, buscar, editar y eliminar.</p>
    </a>
</main>
</body>
</html>
