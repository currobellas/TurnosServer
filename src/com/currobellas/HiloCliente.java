package com.currobellas;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by curro on 18/10/15.
 */
public class HiloCliente extends  Thread {
    Socket s=null;
    private boolean root=false;

    public HiloCliente(Socket s){
        this.s=s;

    }

    @Override
    public void run() {
        String mensaje;
        try{
            Scanner sc=new Scanner(s.getInputStream());

            // TODO: O metemos timeouts o cada cliente en un hilo

            // Recepción de usuario
            mensaje=sc.nextLine();
            String codigo=compruebaUsuario(mensaje);

            // Envío del OK o ERROR
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            pw.println(codigo);
            pw.flush();

            //Si es root comprueba pin con seguridad básica y muy vulnerable
            // TODO mejorar la gestión del root
            if (this.root){
                try {
                    String pin ="";// Integer.parseInt(Conexion.alumnos.get("c").getApellidos());

                    pw.println("pin:");
                    pw.flush();
                    pin=sc.nextLine();
                    if (!pin.equals(Conexion.alumnos.get("C").getApellidos())) {
                        root=false;
                        return;
                    }
                    for (int i=0;i<100;i++)
                        pw.println();
                    pw.flush();
                } catch (Exception ex){
                    root=false;
                    pw.println("ERROR pin");
                    pw.flush();
                    return;
                }

            }


                // Si no hay error seguimos
                if (codigo.equals(Protocolo.OK)) {
                    String usuario = mensaje.split(" ")[1].toUpperCase().trim();
                    Peticion peticion = new Peticion(usuario, 0);
                    do {

                        mensaje = sc.nextLine().toUpperCase().trim();
                        //codigo=compruebaComando(mensaje);
                        switch (mensaje) {
                            case Protocolo.ADD:
                                // todo: Añadir Usuario

                                if (Conexion.existePeticion(peticion)) {
                                    pw.println(Protocolo.ERROR + ": Usuario en lista. No añadido."); // SOlo si se ha podido añadir

                                } else {
                                    if (Conexion.anadePeticion(peticion)) {
                                        Conexion.guardaPeticiones();
                                        pw.println(Protocolo.OK + " Añadido"); // SOlo si se ha podido añadir

                                    } else {
                                        pw.println(Protocolo.ERROR + " Problema al añadir la peticion");

                                    }
                                }
                                pw.flush();


                                break;
                            case Protocolo.DELETE:
                                // todo: Eliminar Usuario

                                if (Conexion.eliminaPeticion(peticion)) {

                                    pw.println(Protocolo.OK + " Borrado");//Solo si se ha podido eliminar
                                    pw.flush();
                                    Conexion.guardaPeticiones();
                                } else {
                                    pw.println(Protocolo.ERROR + ": Usuario no en lista");//Solo si se ha podido eliminar
                                    pw.flush();
                                }
                                // todo Actualizar lista de peticiones
                                break;
                            case Protocolo.LIST:
                                pw.println("Lista de peticiones");
                                //System.err.println(Conexion.peticiones.size());
                                ArrayList<Peticion> listaPeticionesTemporal = Conexion.listaPeticiones();
                                int indice = 0;
                                for (Peticion p : listaPeticionesTemporal) {
                                    Alumno a = Conexion.alumnos.get(p.getDni());
                                    try {
                                        System.out.printf("Usuario: %s  Nombre: %s %s  Hora:%tT\n", p.getDni(), a.getNombre(), a.getApellidos(), p.getHoraPeticion());

                                        pw.printf("%d. Usuario: %s  Nombre: %s %s  Hora:%tT\n", indice, p.getDni(), a.getNombre(), a.getApellidos(), p.getHoraPeticion());
                                        indice++;
                                    } catch (Exception es) {
                                    }
                                }
                                pw.flush();
                                break;
                            case Protocolo.EXIT:
                                root=false;
                                break;
                            case Protocolo.INSERT:
                                if (root){
                                    int posicion=0;

                                    try {
                                        pw.println("usuario a insertar:");
                                        pw.flush();
                                        String alumnoInsertar=sc.nextLine();
                                        if (!compruebaUsuario(alumnoInsertar).equals(Protocolo.OK))
                                            throw new Exception();
                                        Peticion p= new Peticion(alumnoInsertar,0);

                                        pw.println("posición donde insertar:");
                                        pw.flush();
                                        posicion = Integer.parseInt(sc.nextLine());
                                        Conexion.insertaPeticion(p,posicion);

                                        pw.println("Inserción correcta.");
                                        pw.flush();
                                        Conexion.guardaPeticiones();

                                    } catch(Exception ex){
                                        pw.println(Protocolo.ERROR + ": Inserción no efectuada");
                                        pw.flush();
                                    }

                                }
                                break;
                            case Protocolo.DELALL:
                                // Por ahora borrar el archivo
                                break;
                            case Protocolo.DELROOT:
                                if (root){
                                    pw.println("posición a eliminar:");
                                    pw.flush();
                                    try {
                                        int posicion = Integer.parseInt(sc.nextLine());
                                        Conexion.eliminaPeticion(posicion);
                                        pw.println(Protocolo.OK + ": Eliminado");
                                        pw.flush();
                                        Conexion.guardaPeticiones();
                                    } catch(Exception ex){
                                        pw.println(Protocolo.ERROR + ": Eliminación no efectuada");
                                        pw.flush();
                                    }
                                    break;
                                }
                            default: //ERROR
                                pw.println(Protocolo.ERROR + ": Comando no válido");
                                pw.flush();
                                break;
                        }
                    } while (root);
                }

        } catch (IOException ex){
            System.out.println("Error inesperado");

        } finally {
            try {
                s.close();
            }catch (IOException ex){

            }
        }
    }


    /**
     * Comprueba si el mensaje (USER DNI) es correcto y extrae el usuario.
     * Informa si el usurio existe o no. O si no es válido el mensaje
     * @param mensaje
     * @return  OK si _todo está bien.
     *          ERROR02 si no existe usuario.
     *          ERROR01 si mensaje no válido.
     */
    private String compruebaUsuario(String mensaje){

        String[] partes = mensaje.toUpperCase().split(" ");

        // System.out.printf("\nlongitud:%d   _%s___%s_",partes.length,partes[0],partes[1]);

        if (partes.length!=2 || !partes[0].trim().equals(Protocolo.USER))
            return Protocolo.ERROR+"01";

        if (partes[1].trim().toUpperCase().equals(Protocolo.ADMIN)){
            root=true;
            return Protocolo.OK;
        }

        if (!Conexion.alumnos.containsKey(partes[1].trim().toUpperCase()))
            return Protocolo.ERROR+"02";

        return Protocolo.OK;
    }


}
