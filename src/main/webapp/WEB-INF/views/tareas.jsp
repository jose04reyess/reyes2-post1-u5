<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Tareas - Sistema de Seguimiento</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">

        <!-- Encabezado de la Aplicación y Estado de Sesión -->
        <header class="app-header">
            <div>
                <h1>Gestión de Tareas</h1>
                <p style="color: var(--text-muted); font-size: 0.95rem;">Unidad 5: Servlet, JSP, JSTL y Sesión</p>
            </div>
            <div class="user-banner">
                <c:choose>
                    <c:when test="${not empty sessionScope.usuario}">
                        <div class="user-greeting">
                            👤 Bienvenido/a, <strong><c:out value="${sessionScope.usuario}"/></strong>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/tareas" method="post" class="user-form">
                            <input type="hidden" name="accion" value="identificar"/>
                            <input type="text" name="nombre" placeholder="Ingresa tu nombre..." required style="max-width: 180px; padding: 6px 10px; font-size: 0.88rem;"/>
                            <button type="submit" class="btn btn-primary" style="padding: 6px 12px; font-size: 0.88rem;">Identificarse</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </header>

        <!-- Mensajes de Alerta y Notificaciones de Error -->
        <c:if test="${not empty error}">
            <div class="alerta alerta-error">
                ⚠️ <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${param.error == 'no-encontrada'}">
            <div class="alerta alerta-error">
                ⚠️ La tarea solicitada no existe o ha sido eliminada.
            </div>
        </c:if>

        <c:if test="${param.error == 'id-invalido'}">
            <div class="alerta alerta-error">
                ⚠️ El identificador de tarea proporcionado no es válido.
            </div>
        </c:if>

        <!-- Card: Agregar Tarea Rápida -->
        <section class="card">
            <h2>➕ Nueva Tarea</h2>
            <form action="${pageContext.request.contextPath}/tareas" method="post" class="form-agregar">
                <input type="hidden" name="accion" value="agregar"/>
                <input type="text" name="titulo" id="inputTitulo" placeholder="Escribe el título de una nueva tarea..." required autocomplete="off"/>
                <button type="submit" class="btn btn-primary">+ Agregar Tarea</button>
            </form>
        </section>

        <!-- Card: Filtro Multidimensional Combinado -->
        <section class="card">
            <h2>🔍 Filtrado y Búsqueda</h2>
            <form action="${pageContext.request.contextPath}/tareas" method="get" class="filtro">
                <div class="filtro-campo">
                    <label for="q">Buscar por título</label>
                    <input type="text" name="q" id="q" placeholder="Ej: Laboratorio, Servlet..." value="<c:out value='${filtroTexto}'/>"/>
                </div>

                <div class="filtro-campo">
                    <label for="cat">Categoría</label>
                    <select name="cat" id="cat">
                        <option value="">Todas las categorías</option>
                        <c:forEach items="${categorias}" var="c">
                            <option value="${c}" ${c eq filtroCategoria ? 'selected' : ''}>${c}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="filtro-campo">
                    <label for="prioridad">Prioridad</label>
                    <select name="prioridad" id="prioridad">
                        <option value="">Todas las prioridades</option>
                        <option value="Alta" ${filtroPrioridad eq 'Alta' ? 'selected' : ''}>Alta</option>
                        <option value="Media" ${filtroPrioridad eq 'Media' ? 'selected' : ''}>Media</option>
                        <option value="Baja" ${filtroPrioridad eq 'Baja' ? 'selected' : ''}>Baja</option>
                    </select>
                </div>

                <div class="filtro-acciones">
                    <button type="submit" class="btn btn-primary">Filtrar</button>
                    <a href="${pageContext.request.contextPath}/tareas?q=&cat=&prioridad=" class="btn btn-secondary">Limpiar</a>
                </div>
            </form>

            <div class="contador-tareas">
                <span>Resultados de búsqueda: <strong>${fn:length(tareas)}</strong> tarea(s) encontrada(s)</span>
                <c:if test="${not empty filtroTexto or not empty filtroCategoria or not empty filtroPrioridad}">
                    <span style="font-size: 0.85rem; color: var(--primary-color);">Filtro activo recordado en sesión</span>
                </c:if>
            </div>
        </section>

        <!-- Card: Listado Principal de Tareas -->
        <section class="card">
            <h2>📋 Listado de Tareas</h2>
            
            <c:choose>
                <c:when test="${empty tareas}">
                    <div class="empty-state">
                        <p>No se encontraron tareas con los criterios seleccionados.</p>
                        <a href="${pageContext.request.contextPath}/tareas?q=&cat=&prioridad=" class="btn btn-secondary">Ver todas las tareas</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tabla-contenedor">
                        <table>
                            <thead>
                                <tr>
                                    <th style="width: 50px; text-align: center;">#</th>
                                    <th>Título</th>
                                    <th style="width: 130px;">Categoría</th>
                                    <th style="width: 100px; text-align: center;">Prioridad</th>
                                    <th style="width: 120px; text-align: center;">Estado</th>
                                    <th style="width: 110px; text-align: center;">Detalle</th>
                                    <th style="width: 100px; text-align: center;">Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${tareas}" var="t">
                                    <tr class="${t.completada ? 'fila-completada' : ''}">
                                        <td style="text-align: center; font-weight: 600; color: var(--text-muted);">${t.id}</td>
                                        <td>
                                            <span class="${t.completada ? 'completada' : ''}">
                                                <c:out value="${t.titulo}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="categoria-tag"><c:out value="${t.categoria}"/></span>
                                        </td>
                                        <td style="text-align: center;">
                                            <span class="badge badge-${fn:toLowerCase(t.prioridad)}">${t.prioridad}</span>
                                        </td>
                                        <td style="text-align: center;">
                                            <c:choose>
                                                <c:when test="${t.completada}">
                                                    <span class="badge badge-estado-completada">Completada</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-estado-pendiente">Pendiente</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align: center;">
                                            <a href="${pageContext.request.contextPath}/tareas/detalle?id=${t.id}" class="btn-link">Ver Detalle</a>
                                        </td>
                                        <td style="text-align: center;">
                                            <form action="${pageContext.request.contextPath}/tareas" method="post" style="display:inline;" onsubmit="return confirm('¿Desea eliminar la tarea #${t.id}?');">
                                                <input type="hidden" name="accion" value="eliminar"/>
                                                <input type="hidden" name="id" value="${t.id}"/>
                                                <button type="submit" class="btn btn-danger">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </div>
</body>
</html>
