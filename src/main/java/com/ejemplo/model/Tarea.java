package com.ejemplo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Modelo de dominio que representa una Tarea dentro del sistema.
 * Cumple con la especificación JavaBean estándar.
 */
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titulo;
    private boolean completada;
    private String categoria;
    private String prioridad; // "Alta", "Media", "Baja"
    private Date fechaLimite;

    /**
     * Constructor por defecto requerido por la convención JavaBean.
     */
    public Tarea() {
    }

    /**
     * Constructor completo para inicializar una nueva tarea pendiente.
     *
     * @param id          Identificador único de la tarea.
     * @param titulo      Título o descripción corta de la tarea.
     * @param categoria   Categoría temática de la tarea.
     * @param prioridad   Nivel de prioridad ("Alta", "Media", "Baja").
     * @param fechaLimite Fecha límite establecida para la tarea.
     */
    public Tarea(int id, String titulo, String categoria, String prioridad, Date fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.completada = false;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }

    /**
     * Constructor extendido con estado de completitud.
     *
     * @param id          Identificador único de la tarea.
     * @param titulo      Título de la tarea.
     * @param completada  Estado de completitud.
     * @param categoria   Categoría temática.
     * @param prioridad   Prioridad asignada.
     * @param fechaLimite Fecha límite establecida.
     */
    public Tarea(int id, String titulo, boolean completada, String categoria, String prioridad, Date fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.completada = completada;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }

    // --- Getters y Setters JavaBean ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", completada=" + completada +
                ", categoria='" + categoria + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", fechaLimite=" + fechaLimite +
                '}';
    }
}
