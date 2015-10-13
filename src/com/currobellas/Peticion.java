package com.currobellas;

/**
 * Created by curro on 11/10/15.
 */
public class Peticion {
    private String dni;
    private long horaPeticion; // Momento en que se realiza la petición



    public long getHoraPeticion() {
        return horaPeticion;
    }



    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }


    public String getTipo() {
        return tipoPeticion[tipo];
    }

    private int tipo; // 0: corrección;  1: duda; 2: otros

    private final String[] tipoPeticion= {"Corrección", "Dudas", "Otro"};

    public Peticion(String dni, int tipo) {
        this.dni = dni;
        this.tipo = tipo;
        this.horaPeticion=System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object obj) {

        if (!obj.getClass().equals(this.getClass()))
            return false;
        Peticion externa=(Peticion)obj;

        return this.getDni().equals(externa.getDni());
    }
}
