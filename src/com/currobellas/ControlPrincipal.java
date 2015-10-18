package com.currobellas;

import java.io.File;
import java.util.*;

/**
 * Created by curro on 11/10/15.
 */
public class ControlPrincipal {
    private Map<String,Alumno> alumnos = new HashMap<String, Alumno>();
    //private ArrayList<Peticion> peticiones = new ArrayList<Peticion>();

    private Conexion con;

    public ControlPrincipal(){
        String linea;
        try {
            //System.out.println("Working Directory = " + System.getProperty("user.dir"));
            Scanner f = new Scanner(new File("alumnos.csv"));
            while (f.hasNext()) {
                linea = f.nextLine().toUpperCase();
               // System.out.println(linea);
                String[] s = linea.split(",");
                if (s.length==3){
                    Alumno a= new Alumno(s[0], s[2],s[1]);
                    alumnos.put(s[0],a);
                }
            }
            f.close();
        } catch (Exception e) {
            System.err.println("Error de acceso al archivo: " + e.getMessage());
        }
    }

    public void inicio(){
        con=new Conexion(5001, alumnos);
        Scanner sc=new Scanner(System.in);

        System.out.println("Hi");
        con.start();


        System.out.println("\nPulsa Enter para finalizar");
        sc.nextLine();
        con.finalizarConexion();

       /* Peticion p=new Peticion("33",1);
        Peticion q=new Peticion("32",0);
        System.out.println(p.equals("33"));*/
    }

}
