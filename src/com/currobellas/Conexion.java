package com.currobellas;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by curro on 10/10/15.
 * Clase que estará corriendo en segundo plano a la espera de clientes con peticiones.
 *
 */
public class Conexion extends Thread {
    private int puerto = 5001;
    private boolean funcionando=true;
    private ServerSocket ss;
    private Socket s=null;
    private Map<String, Alumno> alumnos;
    private ArrayList<Peticion> peticiones;


    public boolean isFuncionando() {
        return funcionando;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }


    public Conexion() {
    }

    public Conexion(int puerto, Map<String, Alumno> alumnos, ArrayList<Peticion> peticiones) {

        this.puerto = puerto;
        this.alumnos = alumnos;
        this.peticiones = peticiones;
    }

    public void finalizarConexion(){
        setFuncionando(false);
        try {
            this.ss.close();
        }catch(IOException ex){

        }

    }

    @Override
    public void run() {
       // ServerSocket ss;

        DataOutputStream salidaDatos;
       // BufferedReader entradaDatos;
        String mensaje;

        try{
            this.ss=new ServerSocket(this.puerto);
            //TODO Comprobación de puerto ocupado
            System.out.printf("Servidor a la espera en el puerto %d", this.puerto);
            while (funcionando) {
                s=ss.accept();
                estableceConexion(s);
            }
        }catch (IOException ex){

                System.out.println(isFuncionando()?"Error de conexión o puerto ocupado":"Finalizando el servicio");

        }
    }

    private void estableceConexion(Socket s) throws IOException{
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

                       if (existePeticion(peticion)){
                           pw.println(Protocolo.ERROR+ ": Usuario en lista. No añadido."); // SOlo si se ha podido añadir
                           pw.flush();
                       } else {
                           peticiones.add(peticion);
                           pw.println(Protocolo.OK+ " Añadido"); // SOlo si se ha podido añadir
                           pw.flush();
                       }


                       break;
                   case Protocolo.DELETE:
                   // todo: Eliminar Usuario

                       if(peticiones.remove(peticion)) {

                           pw.println(Protocolo.OK + " Borrado");//Solo si se ha podido eliminar
                           pw.flush();
                       } else {
                           pw.println(Protocolo.ERROR + ": Usuario no en lista");//Solo si se ha podido eliminar
                           pw.flush();
                       }
                        // todo Actualizar lista de peticiones
                       break;
                   case Protocolo.LIST:
                       pw.println("Lista de peticiones");
                       for (Peticion p:peticiones){
                           Alumno a=alumnos.get(p.getDni());
                           pw.printf("Usuario: %s  Nombre: %s %s  Hora:%tT\n",p.getDni(),a.getNombre(),a.getApellidos(),p.getHoraPeticion());
                       }
                       pw.flush();
                       break;
                   default: //ERROR
                       pw.println(Protocolo.ERROR+": Comando no válido");
                       pw.flush();
                       break;
               }

           }

       } finally {
           s.close();
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

        if (!alumnos.containsKey(partes[1].trim().toUpperCase()))
            return Protocolo.ERROR+"02";

        return Protocolo.OK;
    }

    private boolean existePeticion(Peticion peticion){
        for (Peticion p:peticiones){
            if (p.equals(peticion))
                return true;

        }
        return false;
    }

    private boolean existePeticion(String usuario){
        Peticion p= new Peticion(usuario,0);
        return existePeticion(p);
    }
}
