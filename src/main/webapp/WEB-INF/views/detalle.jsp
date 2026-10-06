<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle de Tarea #${tarea.id} - Gestión de Tareas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">

        <!-- Enlace de retorno con preservación de contexto -->
        <a href="${pageContext.request.contextPath}/tareas" class="btn-volver">&larr; Volver al listado de tareas</a>

        <div class="tabla-detalle-card">
            <section class="card">
                <h2>📌 Información de la Tarea #${tarea.id}</h2>

                <table class="detalle">
                    <tbody>
                        <tr>
                            <th>Identificador (ID)</th>
                            <td style="font-weight: 600; color: var(--text-muted);">#${tarea.id}</td>
                        </tr>
                        <tr>
                            <th>Título de la Tarea</th>
                            <td style="font-weight: 600; font-size: 1.05rem;">
                                <span class="${tarea.completada ? 'completada' : ''}">
                                    <c:out value="${tarea.titulo}"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th>Categoría Temática</th>
                            <td>
                                <span class="categoria-tag"><c:out value="${tarea.categoria}"/></span>
                            </td>
                        </tr>
                        <tr>
                            <th>Nivel de Prioridad</th>
                            <td>
                                <span class="badge badge-${fn:toLowerCase(tarea.prioridad)}">${tarea.prioridad}</span>
                            </td>
                        </tr>
                        <tr>
                            <th>Fecha Límite</th>
                            <td>
                                📅 <fmt:formatDate value="${tarea.fechaLimite}" pattern="dd/MM/yyyy"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Estado Actual</th>
                            <td>
                                <c:choose>
                                    <c:when test="${tarea.completada}">
                                        <span class="badge badge-estado-completada">Completada</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-estado-pendiente">Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div class="detalle-acciones">
                    <c:choose>
                        <c:when test="${not tarea.completada}">
                            <form action="${pageContext.request.contextPath}/tareas" method="post" style="display: inline;">
                                <input type="hidden" name="accion" value="completar"/>
                                <input type="hidden" name="id" value="${tarea.id}"/>
                                <button type="submit" class="btn btn-success">✔ Marcar como Completada</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="alerta alerta-success" style="margin-bottom: 0; width: 100%;">
                                ✨ Esta tarea ha sido completada exitosamente.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>

    </div>
</body>
</html>
