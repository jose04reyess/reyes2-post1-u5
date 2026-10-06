package com.ejemplo.servlet;

import com.ejemplo.model.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Controlador principal para la gestión del ciclo de vida de tareas.
 * Administra el listado, filtrado multidimensional, inserción con validación (PRG),
 * eliminación y persistencia en sesión y applicationScope.
 */
@WebServlet(name = "TareasServlet", urlPatterns = {"/tareas"})
public class TareasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Lista en memoria compartida por el ciclo de vida del servlet / applicationScope
    private final List<Tarea> tareas = new CopyOnWriteArrayList<>();
    private int contadorId = 1;

    /**
     * Método auxiliar para calcular fechas límite sumando días a una fecha base.
     */
    private Date sumarDias(Date base, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Date hoy = new Date();

        // Carga de 4 tareas iniciales con categorías y prioridades diferenciadas
        synchronized (tareas) {
            tareas.clear();
            tareas.add(new Tarea(contadorId++, "Estudiar Servlets y JSP", "Estudio", "Alta", sumarDias(hoy, 2)));
            tareas.add(new Tarea(contadorId++, "Completar Laboratorio Unidad 5", "Evaluación", "Alta", sumarDias(hoy, 4)));
            tareas.add(new Tarea(contadorId++, "Repasar directivas JSTL y Expression Language", "Estudio", "Media", sumarDias(hoy, 6)));
            tareas.add(new Tarea(contadorId++, "Preparar Examen Parcial de Jakarta EE", "Evaluación", "Baja", sumarDias(hoy, 10)));
        }

        // Publicar lista en applicationScope (ServletContext) para consumo global del sistema
        getServletContext().setAttribute("tareas", tareas);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        // 1. Obtener parámetros desde la URL
        String paramQ = req.getParameter("q");
        String paramCat = req.getParameter("cat");
        String paramPrioridad = req.getParameter("prioridad");

        String q;
        String cat;
        String prioridad;

        // 2. Comprobar si los filtros vienen explícitamente en la URL
        boolean vieneFiltroEnUrl = (paramQ != null || paramCat != null || paramPrioridad != null);

        if (vieneFiltroEnUrl) {
            // Se actualiza el filtro activo y se guarda en HttpSession para recordar estado
            q = paramQ != null ? paramQ.trim() : "";
            cat = paramCat != null ? paramCat.trim() : "";
            prioridad = paramPrioridad != null ? paramPrioridad.trim() : "";

            session.setAttribute("filtroTexto", q);
            session.setAttribute("filtroCategoria", cat);
            session.setAttribute("filtroPrioridad", prioridad);
        } else {
            // Se restaura el último filtro guardado en la sesión si existiese
            Object sQ = session.getAttribute("filtroTexto");
            Object sCat = session.getAttribute("filtroCategoria");
            Object sPrio = session.getAttribute("filtroPrioridad");

            q = sQ != null ? sQ.toString().trim() : "";
            cat = sCat != null ? sCat.toString().trim() : "";
            prioridad = sPrio != null ? sPrio.toString().trim() : "";
        }

        // 3. Filtrado multidimensional combinando los 3 criterios
        List<Tarea> tareasFiltradas = tareas.stream()
                .filter(t -> {
                    // Criterio de búsqueda por texto (case-insensitive)
                    if (!q.isEmpty()) {
                        if (t.getTitulo() == null || !t.getTitulo().toLowerCase().contains(q.toLowerCase())) {
                            return false;
                        }
                    }
                    // Criterio de búsqueda por categoría
                    if (!cat.isEmpty() && !cat.equalsIgnoreCase("Todas")) {
                        if (t.getCategoria() == null || !t.getCategoria().equalsIgnoreCase(cat)) {
                            return false;
                        }
                    }
                    // Criterio de búsqueda por prioridad
                    if (!prioridad.isEmpty() && !prioridad.equalsIgnoreCase("Todas")) {
                        if (t.getPrioridad() == null || !t.getPrioridad().equalsIgnoreCase(prioridad)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 4. Extracción de categorías únicas ordenadas alfabéticamente
        List<String> categorias = tareas.stream()
                .map(Tarea::getCategoria)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 5. Asignar atributos en requestScope para la vista
        req.setAttribute("tareas", tareasFiltradas);
        req.setAttribute("categorias", categorias);
        req.setAttribute("filtroTexto", q);
        req.setAttribute("filtroCategoria", cat);
        req.setAttribute("filtroPrioridad", prioridad);

        // 6. Despacho por Forward hacia la vista JSP
        req.getRequestDispatcher("/WEB-INF/views/tareas.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String accion = req.getParameter("accion");
        if (accion == null) {
            resp.sendRedirect(req.getContextPath() + "/tareas");
            return;
        }

        switch (accion) {
            case "agregar":
                procesarAgregar(req, resp);
                break;

            case "eliminar":
                procesarEliminar(req, resp);
                break;

            case "completar":
                procesarCompletar(req, resp);
                break;

            case "identificar":
                procesarIdentificar(req, resp);
                break;

            default:
                resp.sendRedirect(req.getContextPath() + "/tareas");
                break;
        }
    }

    /**
     * Procesa la inserción de una tarea aplicando validación y el patrón Post-Redirect-Get (PRG).
     */
    private void procesarAgregar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String titulo = req.getParameter("titulo");

        // Validación: el título no puede estar vacío
        if (titulo == null || titulo.trim().isEmpty()) {
            req.setAttribute("error", "El título de la tarea no puede estar vacío.");

            // Preparar el contexto de la vista para renderizar el mensaje de error con los datos
            List<String> categorias = tareas.stream()
                    .map(Tarea::getCategoria)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            req.setAttribute("tareas", new ArrayList<>(tareas));
            req.setAttribute("categorias", categorias);
            req.setAttribute("filtroTexto", "");
            req.setAttribute("filtroCategoria", "");
            req.setAttribute("filtroPrioridad", "");

            req.getRequestDispatcher("/WEB-INF/views/tareas.jsp").forward(req, resp);
            return;
        }

        // Creación de nueva tarea con valores por defecto y fecha a +5 días
        int nuevoId;
        synchronized (this) {
            nuevoId = contadorId++;
        }

        Tarea nuevaTarea = new Tarea(nuevoId, titulo.trim(), "General", "Media", sumarDias(new Date(), 5));
        tareas.add(nuevaTarea);

        // Patrón Post-Redirect-Get (PRG) para evitar reenvío duplicado del formulario
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    /**
     * Procesa la eliminación de una tarea por su identificador.
     */
    private void procesarEliminar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                tareas.removeIf(t -> t.getId() == id);
            } catch (NumberFormatException ignored) {
                // Manejo silencioso o id inválido
            }
        }
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    /**
     * Procesa el cambio de estado de una tarea a completada.
     */
    private void procesarCompletar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                tareas.stream()
                        .filter(t -> t.getId() == id)
                        .findFirst()
                        .ifPresent(t -> t.setCompletada(true));
            } catch (NumberFormatException ignored) {
                // Manejo silencioso o id inválido
            }
        }
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    /**
     * Registra el nombre del usuario en HttpSession.
     */
    private void procesarIdentificar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String nombre = req.getParameter("nombre");
        if (nombre != null && !nombre.trim().isEmpty()) {
            req.getSession().setAttribute("usuario", nombre.trim());
        }
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }
}
