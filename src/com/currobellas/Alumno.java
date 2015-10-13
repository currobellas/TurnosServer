package com.currobellas;

/**
 * Clase para gestión de los alumnos que estén dados de alta en la
 * lista de alumnos y también en la lista de peticiones.
 * Created by curro on 10/10/15.
 *
 * Guarda:
 * DNI del alumno. Será la clave y su usuario para conexión.
 * Nombre y Aplleidos del alumno.
 *
 */
public class Alumno {
    private String dni;
    private String nombre;
    private String apellidos;

    public Alumno(String dni, String nombre, String apellidos) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public Alumno() {
        this.dni = "";
        this.nombre = "";
        this.apellidos = "";
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
