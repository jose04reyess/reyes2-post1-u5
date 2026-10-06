# Post-contenido — Unidad 5: Gestión de Tareas con Servlet, JSP, JSTL y Sesión

**Autor:** Reyes  
**Tecnologías:** Java 17 | Jakarta Servlet 6.0 | JSTL 3.0 | Maven | Apache Tomcat 10+

---

## 📌 Descripción General del Proyecto

Este proyecto implementa una solución web robusta para la **Gestión de Tareas**, desarrollada siguiendo la especificación **Jakarta EE 10** bajo el patrón arquitectónico **Modelo-Vista-Controlador (MVC)**. El sistema combina el procesamiento dinámico en servidor mediante Servlets, renderizado declarativo en vistas JSP con directivas JSTL y Expression Language (EL), administración de estado en memoria y persistencia en sesión.

---

## 🚀 Resumen Técnico de Implementación

### Parte 1: Ciclo de Vida GET/POST, Validación y Patrón PRG
- **Controlador Principal (`TareasServlet`):** Manejo centralizado del flujo de tareas en `/tareas`.
- **Carga Inicial y Ámbito Global:** Inicialización de 4 tareas de prueba en el método `init()` con categorías ("Estudio", "Evaluación"), prioridades escalonadas ("Alta", "Media", "Baja") y fechas límite calculadas dinámicamente.
- **Validación del Lado del Servidor:** Comprobación de que el título de la tarea no sea nulo ni consista únicamente en espacios en blanco. Si la validación falla, se retorna un mensaje descriptivo de error mediante `forward` manteniendo el estado.
- **Patrón Post-Redirect-Get (PRG):** Implementación estricta de `response.sendRedirect(...)` tras mutaciones exitosas (crear, eliminar, completar e identificación de usuario), evitando duplicación de acciones accidentales por recarga del navegador (tecla F5).

### Parte 2: Filtrado Multidimensional, Detalle, `applicationScope` y Sesión
- **Filtrado Combinado:** Capacidad de filtrar simultáneamente por término de búsqueda (insensible a mayúsculas/minúsculas), categoría temática y nivel de prioridad.
- **Persistencia de Filtro y Usuario en `HttpSession`:** Si los parámetros de búsqueda se envían en el query string, se aplican y almacenan en la sesión HTTP. Si el usuario navega directamente, se restablece de forma transparente su último filtro seleccionado.
- **Vista de Detalle (`DetalleTareaServlet`):** Consulta idempotente de tareas individuales por ID en `/tareas/detalle?id={id}`, extrayendo el objeto compartido desde `applicationScope` y transfiriéndolo a `/WEB-INF/views/detalle.jsp` vía `forward`.
- **Acción de Completar Tarea:** Interfaz dedicada para marcar tareas como completadas mediante una solicitud POST segura.

---

## 📐 Decisiones de Diseño Arquitectónico

### 1. Lista de tareas como variable de instancia y `applicationScope`
* **Fundamento:** Para mantener un almacenamiento en memoria centralizado y accesible entre diferentes controladores (`TareasServlet` y `DetalleTareaServlet`), la lista `List<Tarea>` se mantiene como variable de instancia thread-safe (`CopyOnWriteArrayList`) en `TareasServlet` y se publica en el contexto global de la aplicación (`getServletContext().setAttribute("tareas", tareas)`).
* **Beneficio:** Permite que `DetalleTareaServlet` consulte y resuelva tareas por su identificador directamente desde `applicationScope` (`ServletContext`) sin instanciar réplicas de datos ni recurrir a acoplamientos rígidos entre servlets.

### 2. Uso de `HttpSession` vs `HttpServletRequest` para filtros y usuario
* **Fundamento:** Se diferencian claramente las responsabilidades de los ámbitos de datos de Jakarta EE:
  - **`HttpSession`:** Empleado para almacenar información con ciclo de vida conversacional que debe persistir entre múltiples peticiones independientes, como el nombre del usuario identificado (`usuario`) y las preferencias de búsqueda (`filtroTexto`, `filtroCategoria`, `filtroPrioridad`).
  - **`HttpServletRequest`:** Empleado exclusivamente para los datos efímeros correspondientes a la renderización de la petición actual, como la sublista filtrada (`tareas`), las categorías únicas extraídas (`categorias`) y los mensajes de error puntuales (`error`).
* **Beneficio:** Proporciona una experiencia de usuario fluida donde los filtros no se pierden al inspeccionar detalles o interactuar con otras secciones, manteniendo la capa de presentación desacoplada y libre de fugas de estado.

### 3. Uso de `Forward` en el Servlet de Detalle vs Redirección (PRG)
* **Fundamento:** 
  - La operación de consulta de detalle (`/tareas/detalle?id=X`) es una operación **idempotente y de solo lectura** (método HTTP GET). Por ello, el servlet despacha directamente el flujo interno hacia `/WEB-INF/views/detalle.jsp` utilizando `req.getRequestDispatcher(...).forward(req, resp)`. Esto permite pasar la instancia completa de `Tarea` dentro del `requestScope` de forma eficiente en memoria sin exponer la ruta privada `WEB-INF`.
  - En contraste, las operaciones de mutación de estado (agregar, eliminar, completar) utilizan el método HTTP POST y finalizan con `resp.sendRedirect(...)` (patrón **PRG**).
* **Beneficio:** Optimización de recursos en operaciones GET y protección contra envíos duplicados de formularios en operaciones POST.

### 4. Extracción de estilos a `css/estilos.css`
* **Fundamento:** Se modularizaron todas las directivas visuales en una hoja de estilo externa ubicada en `src/main/webapp/css/estilos.css`, referenciada por ambas vistas JSP (`tareas.jsp` y `detalle.jsp`) mediante `${pageContext.request.contextPath}/css/estilos.css`.
* **Beneficio:** Garantiza coherencia visual en toda la aplicación (paleta corporativa basada en `#065A82`, tipografías legibles, alertas `#C62828`/`#FFEBEE`, tablas colapsadas, estados tachados `.completada` e insignias de prioridad), permitiendo el almacenamiento en caché por el navegador y eliminando la duplicidad de estilos CSS embebidos.

---

## 📂 Estructura del Proyecto

```plaintext
reyes2-post1-u5/
├── pom.xml
├── README.md
├── img/
│   ├── captura-01.png
│   └── captura-02.png
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── ejemplo/
        │           ├── model/
        │           │   └── Tarea.java
        │           └── servlet/
        │               ├── DetalleTareaServlet.java
        │               └── TareasServlet.java
        └── webapp/
            ├── index.jsp
            ├── css/
            │   └── estilos.css
            └── WEB-INF/
                ├── web.xml
                └── views/
                    ├── detalle.jsp
                    └── tareas.jsp
```

---

## 📸 Evidencias Visuales de la Aplicación

### Captura 1: Listado Principal, Identificación y Filtrado Combinado
Vista principal con saludo de sesión, formulario para agregar tareas, filtros dinámicos y tabla de tareas con badges de prioridad y acciones:

![Listado de Tareas y Filtros](img/captura-01.png)

---

### Captura 2: Vista de Detalle y Cambio de Estado
Vista vertical con la información detallada de la tarea, formato de fecha (`dd/MM/yyyy`) y botón para marcar como completada:

![Detalle de Tarea](img/captura-02.png)

---

## 🛠️ Instrucciones de Compilación y Despliegue

### Requisitos Previos
- **Java Development Kit (JDK):** Versión 17 o superior.
- **Apache Maven:** Versión 3.8 o superior.
- **Servidor de Aplicaciones:** Apache Tomcat 10.1+ (compatible con Jakarta EE 10 / Servlet 6.0).

### 1. Compilación del Proyecto
Ejecutar el siguiente comando en la raíz del proyecto para limpiar, compilar y empaquetar el artefacto WAR:

```bash
mvn clean package
```

El empaquetado generará el archivo `target/gestion-tareas.war`.

### 2. Despliegue en Apache Tomcat 10+
1. Copiar el archivo generado `target/gestion-tareas.war` al directorio `webapps/` de la instalación de Tomcat:
   ```bash
   cp target/gestion-tareas.war $CATALINA_HOME/webapps/
   ```
2. Iniciar el servidor Tomcat:
   - En Windows: `%CATALINA_HOME%\bin\startup.bat`
   - En Linux/macOS: `$CATALINA_HOME/bin/startup.sh`
3. Abrir el navegador e ingresar a la aplicación:
   ```
   http://localhost:8080/gestion-tareas/
   ```
   *(La raíz redirigirá automáticamente a `/gestion-tareas/tareas` a través de `index.jsp`)*.