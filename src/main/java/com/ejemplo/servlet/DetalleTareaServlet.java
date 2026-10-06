package com.ejemplo.servlet;

import com.ejemplo.model.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet encargado de la consulta detallada de una tarea específica.
 * Implementa lectura idempotente y reenvío interno (Forward) hacia la vista de detalle.
 */
@WebServlet(name = "DetalleTareaServlet", urlPatterns = {"/tareas/detalle"})
public class DetalleTareaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");

        // 1. Validación de parámetro ID
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=id-invalido");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=id-invalido");
            return;
        }

        // 2. Obtener lista global de tareas desde applicationScope (ServletContext)
        @SuppressWarnings("unchecked")
        List<Tarea> listaTareas = (List<Tarea>) getServletContext().getAttribute("tareas");

        if (listaTareas == null) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=no-encontrada");
            return;
        }

        // 3. Buscar la tarea correspondiente por su ID
        Optional<Tarea> tareaOpt = listaTareas.stream()
                .filter(t -> t.getId() == id)
                .findFirst();

        if (tareaOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=no-encontrada");
            return;
        }

        // 4. Adjuntar la tarea encontrada al requestScope y despachar por Forward a la vista
        req.setAttribute("tarea", tareaOpt.get());
        req.getRequestDispatcher("/WEB-INF/views/detalle.jsp").forward(req, resp);
    }
}
