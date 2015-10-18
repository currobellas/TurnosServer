package com.currobellas;

import java.io.*;
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
    public  static Map<String, Alumno> alumnos;
    public static ArrayList<Peticion> peticiones;


    public boolean isFuncionando() {
        return funcionando;
    }

    public void setFuncionando(boolean funcionando) {
        this.funcionando = funcionando;
    }


    public Conexion() {
    }

    public Conexion(int puerto, Map<String, Alumno> alumnos) {

        this.puerto = puerto;
        Conexion.alumnos = alumnos;
       // this.peticiones = peticiones;
        this.leePeticiones();
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
                HiloCliente cliente= new HiloCliente(s);
                cliente.start();
            }
        }catch (IOException ex){

                System.out.println(isFuncionando()?"Error de conexión o puerto ocupado":"Finalizando el servicio");

        }
    }

    private void estableceConexion(Socket s) throws IOException{


    }








    public static synchronized boolean guardaPeticiones(){
        try {
            ObjectOutputStream archivo = new ObjectOutputStream(new FileOutputStream("peticiones.obj"));
            for (Peticion p: peticiones)
                archivo.writeObject(p);
            archivo.writeObject(new Peticion("EOF",0)); // Marca de fin de archivo
            archivo.close();
            return true;
        } catch (IOException ex){
            System.err.println("Error al guardar peticiones");
        }
        return false;
    }

    private boolean leePeticiones(){
        peticiones=new ArrayList<Peticion>();
        try {
            ObjectInputStream archivo = new ObjectInputStream(new FileInputStream("peticiones.obj"));
            Peticion p = null;
            p = (Peticion) archivo.readObject();
            while (p != null && !p.getDni().equals("EOF")) {
                peticiones.add(p);
                System.err.println(p.getDni());
                p = (Peticion) archivo.readObject();

            }
            archivo.close();
        }catch(EOFException ex){

        }catch(IOException ex){
            System.err.println("Error al leer el archivo o archivo inexistente\n"+ex.getMessage());
        }catch(ClassNotFoundException ex){
            System.err.println("Error de serialización a leer el archivo");
        }

        return false;
    }

    public static  synchronized boolean eliminaPeticion(Peticion peticion){
        return peticiones.remove(peticion);

    }

    public static synchronized boolean anadePeticion(Peticion peticion){
        return peticiones.add(peticion);
    }

    public static synchronized ArrayList<Peticion> listaPeticiones(){
        return peticiones;
    }

    public static synchronized boolean existePeticion(Peticion peticion){
        for (Peticion p:Conexion.peticiones){
            if (p.equals(peticion))
                return true;

        }
        return false;
    }
    public static synchronized boolean existePeticion(String usuario){
        Peticion p= new Peticion(usuario,0);
        return existePeticion(p);
    }
}
