package PackageServidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    static ServerSocket Servidor;
    static Socket socketConexion;
    static PrintWriter escritura;
    static BufferedReader lectura;
    static String User;
    static String Password;
    static boolean estado;
    static String directorioRaiz;
    static String directorioActual;

    public static void LOGIN(){
        try {
            String completo = new String();
            String[] partes;
            while (true) {
                completo = lectura.readLine().trim();
                if(completo.contains(" "))
                    partes = completo.split(" ");
                else {
                    escritura.println("501 Comando invalido");
                    escritura.flush();
                    continue;
                }

                if (partes[0].equals("admin") && partes.length == 2) {
                    User = partes[1];
                    escritura.println("331 Password requerido para ingresar a la cuenta '" + partes[0] + "'");
                } else if (partes[0].equals("pass")) {
                    Password = partes[1];
                    if(User.equals("redes") && Password.equals("1234")) {
                        estado = true;
                        escritura.println("230 Log in OK");
                        escritura.flush();
                        System.out.println("Conexion establecida\n");
                        break;
                    } else {
                        escritura.println("430 Fallo en Autentificacion");
                        User = "";
                        Password = "";
                    }
                } else {
                    escritura.println("501 Comando invalido");
                }
                escritura.flush();
            }
        }catch (Exception e){
            System.out.println("Error en Login: " + e);
            QUIT();
        }
    }

    public static void CD(String carpeta){
        String directorio = new String();
        String temporal;
        if(carpeta.contains(" ")){
            directorio = carpeta.split(" ")[1];
            if(directorio.equals("..")){
                if(!directorioActual.equals(directorioRaiz)) {
                    temporal = directorioActual.substring(directorioActual.length() - 1);
                    directorioActual = directorioActual.substring(temporal.lastIndexOf("/"));
                }
                escritura.println("250 El nuevo directorio de trabajo es " + directorioActual);
            }
            else{
                File archivo = new File(directorioActual + directorio + "/");
                if (archivo.exists()){
                    directorioActual = directorioActual + directorio + "/";
                    escritura.println("250 El nuevo directorio de trabajo es " + directorioActual);
                }
                else
                    escritura.println("550 Directorio no encontrado");
            }
        } else {
            escritura.println("250 El nuevo directorio de trabajo es " + directorioActual);
        }
        System.out.println(directorioActual);
        escritura.flush();
    }

    public static void LS(String carpeta){
        String directorio = new String();
        String nombre = new String();
        if(carpeta.contains(" ")){
            nombre = carpeta.split(" ")[1];
            directorio = directorioActual + nombre;
        }
        else
            directorio = directorioActual;

        File archivo = new File(directorio);
        System.out.println(directorio);
        if (archivo.exists()){
            File[] ficheros = archivo.listFiles();
            ArrayList<String> Archivos = new ArrayList<>();
            for (int i = 0; i < ficheros.length; i++){
                Archivos.add(ficheros[i].getName());
            }

            escritura.println("150 Mostrando lista del directorio " + nombre);
            escritura.flush();
            for(String file: Archivos){
                escritura.println(file);
            }
            escritura.flush();
            escritura.println("226 Listado Completado");
            escritura.flush();
        }
        else {
            escritura.println("550 Directorio Invalido");
            escritura.flush();
        }
    }

    public static void GET(){

    }

    public static void PUT(){

    }

    public static void QUIT(){
        directorioActual = directorioRaiz;
        estado = false;
        try {
            socketConexion.close();
        } catch (Exception e){
            System.out.println("Error: " + e);
        }
    }


    public static void main(String args[]) throws Exception
    {
        int puerto = 21;
        directorioRaiz = "Directorio_FTP/";
        directorioActual = "Directorio_FTP/";
        Servidor = null;
        try {
            Servidor = new ServerSocket(puerto);
        } catch (IOException e) {
            System.out.println("No se ha podido levantar el servidor");
            System.exit ( 0 );
        }
        System.out.println("Servidor a la escucha...");

        while (true) {
            socketConexion = null;
            try {
                socketConexion = Servidor.accept();
                System.out.println("Cliente entrante...");

                escritura = new PrintWriter(new OutputStreamWriter(socketConexion.getOutputStream()));
                lectura = new BufferedReader(new InputStreamReader(socketConexion.getInputStream()));
                escritura.println("220 Hola, Soy el servidor FTP.");
                escritura.flush();
            } catch (IOException e) {
                System.out.println("No se ha podido crear el nuevo socket");
                QUIT();
            }

            LOGIN();
            System.out.println("Esperando comandos...");
            String comando = new String();

            while(estado){
                try {
                    comando = lectura.readLine().trim();
                }catch (IOException e) {
                    System.out.println("Error: " + e);
                    escritura.println("501 Error de sintaxis");
                }

                if(comando.equals("QUIT")){
                    QUIT();
                    break;
                }
                else if(comando.contains("LS")){
                    LS(comando);
                }
                else if(comando.contains("CD")){
                    CD(comando);
                }
                else
                    escritura.println("502 Comando no implementado");
            }
        }
    }
}