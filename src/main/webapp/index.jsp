<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Redirección HTTP inicial hacia el controlador principal de tareas
    response.sendRedirect(request.getContextPath() + "/tareas");
%>
