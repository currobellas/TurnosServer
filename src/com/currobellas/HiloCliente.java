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

            // Si no hay error seguimos
            if (codigo.equals(Protocolo.OK)){
                String usuario=mensaje.split(" ")[1].toUpperCase().trim();
                Peticion peticion= new Peticion(usuario, 0);
                mensaje= sc.nextLine().toUpperCase().trim();
                //codigo=compruebaComando(mensaje);
                switch (mensaje){
                    case Protocolo.ADD:
                        // todo: Añadir Usuario

                        if (Conexion.existePeticion(peticion)){
                            pw.println(Protocolo.ERROR+ ": Usuario en lista. No añadido."); // SOlo si se ha podido añadir

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

                        if(Conexion.eliminaPeticion(peticion)) {

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
                        ArrayList<Peticion> listaPeticionesTemporal= Conexion.listaPeticiones();
                        for (Peticion p:listaPeticionesTemporal){
                            Alumno a=Conexion.alumnos.get(p.getDni());
                            try {
                                System.out.printf("Usuario: %s  Nombre: %s %s  Hora:%tT\n", p.getDni(), a.getNombre(), a.getApellidos(), p.getHoraPeticion());

                                pw.printf("Usuario: %s  Nombre: %s %s  Hora:%tT\n", p.getDni(), a.getNombre(), a.getApellidos(), p.getHoraPeticion());
                            }catch(Exception es){}
                        }
                        pw.flush();
                        break;
                    default: //ERROR
                        pw.println(Protocolo.ERROR+": Comando no válido");
                        pw.flush();
                        break;
                }

            }

        } catch (IOException ex){

        }
        finally {
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

        if (!Conexion.alumnos.containsKey(partes[1].trim().toUpperCase()))
            return Protocolo.ERROR+"02";

        return Protocolo.OK;
    }


}
