package com.currobellas;

/**
 * Created by curro on 10/10/15.
 */
public final class Protocolo {

    /* Protocolo.
    1º El cliente que se conecta manda USER+DNI
    2º El servidor le envía el OK o el ERROR 1 o 2
    3º El cliente le envía un comando al servidor:
            ADD: Añade al usuario al final de la lista
            DEL: Borra el cliente de la lista
    4º El servidor responde con un OK o con ERROR indicando el número de error
    5º Fin de conexión

    ERRORES
    1 Mensaje no valido
    2 No existe usuario
    * */


    public static final String USER="USER";
    public static final String ADD="ADD";
    public static final String DELETE="DEL";
    public static final String LIST="LIST";
    public static final String ADMIN="ROOT";
    public static final String DELROOT="DR"; // El root puede usar este comando para eliminar cualquier entrada indicando el número
    public static final String EXIT="EXIT"; // sale de la cuenta de root
    public static final String INSERT="INSERT"; // El root puede insertar una nueva petición donde quiera
    public static final String DELALL="DELALL"; //El root puede borrar todo el listado de peticiones

    public static final String OK="OK";
    public static final String ERROR="ERROR";

    private Protocolo(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
