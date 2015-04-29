package PackageServidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    static ServerSocket Servidor;
    static ServerSocket ServidorData;
    static Socket socketConexion;
    static PrintWriter escritura;
    static BufferedReader lectura;
    static String User;
    static String Password;
    static boolean estado;
    static String directorioRaiz;
    static String directorioActual;
    static DataInputStream flujoEntrada;
    static DataOutputStream flujoSalida;

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
        String temporal[];
        int largo = 0;
        if(carpeta.contains(" ")){
            directorio = carpeta.split(" ")[1];
            if(directorio.equals("..")){
                if(!directorioActual.equals(directorioRaiz)) {
                    temporal = directorioActual.split("/");
                    largo = temporal[temporal.length - 1].length() + 1;
                    directorioActual = directorioActual.substring(0, directorioActual.length() - largo);
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

    public static void GET(String comando) throws Exception{
        String filename = comando.split(" ")[1];
        File archivo = new File(directorioActual + filename);

        if(!archivo.exists()) {
            escritura.println("550 El archivo no existe");
            escritura.flush();
            return;
        }
        escritura.println("150 Datos de conexion para " + filename);
        escritura.flush();

        Socket socketData;
        try {
            socketData = ServidorData.accept();
            flujoEntrada = new DataInputStream(socketData.getInputStream());
            flujoSalida = new DataOutputStream(socketData.getOutputStream());
        }catch (Exception e){
            System.out.println(e);
            return;
        }

        try {
            FileInputStream fin = new FileInputStream(archivo);
            int largo;
            do {
                largo = fin.read();
                flujoSalida.writeUTF(String.valueOf(largo));
            }
            while (largo != -1);
            fin.close();
        }catch (Exception e){
            System.out.println(e);
        }finally {
            socketData.close();
        }

        System.out.println(filename + " enviado correctamente");
        escritura.println("226 Transferencia completada");
        escritura.flush();
    }

    public static void PUT(String comando) throws Exception{
        String filename = comando.split(" ")[1];
        File archivo = new File(directorioActual + filename);

        if(archivo.exists()) {
            escritura.println("552 Archivo ya existente en el servidor");
            escritura.flush();
            return;
        }
        escritura.println("350 Enviar Archivo");
        escritura.flush();

        Socket socketData;
        try {
            socketData = ServidorData.accept();
            flujoEntrada = new DataInputStream(socketData.getInputStream());
            flujoSalida = new DataOutputStream(socketData.getOutputStream());
        }catch (Exception e){
            System.out.println(e);
            return;
        }

        try {
            FileOutputStream fout = new FileOutputStream(archivo);
            int largo;
            String temp;
            do {
                temp = flujoEntrada.readUTF();
                largo = Integer.parseInt(temp);
                if (largo != -1) {
                    fout.write(largo);
                }
            } while (largo != -1);
            fout.close();
        }catch (Exception e){
            System.out.println(e);
            return;
        } finally {
            socketData.close();
        }

        System.out.println(filename + " cargado correctamente");
        escritura.println("226 " + filename + " subido correctamente");
        escritura.flush();
    }

    public static void QUIT(){
        directorioActual = directorioRaiz;
        estado = false;
        escritura.println("221 Sesion terminada por el usuario");
        escritura.flush();
        try {
            socketConexion.close();
            System.out.println("Conexion finalizada");
            System.out.println("----------------------------------");
        } catch (Exception e){
            System.out.println("Error: " + e);
            escritura.println(e);
            escritura.flush();
        }
    }


    public static void main(String args[]) throws Exception
    {
        directorioRaiz = "Directorio_FTP/";
        directorioActual = "Directorio_FTP/";
        Servidor = null;
        Servidor = null;
        try {
            Servidor = new ServerSocket( 21 );
            ServidorData = new ServerSocket( 20 );
        } catch (IOException e) {
            System.out.println("No se ha podido levantar el servidor");
            System.exit ( 0 );
        }

        while (true) {
            System.out.println("Servidor a la escucha...");
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
                else if(comando.contains("GET")){
                    GET(comando);
                }
                else if(comando.contains("PUT")){
                    PUT(comando);
                }
                else
                    escritura.println("502 Comando no implementado");
            }
        }
    }
}