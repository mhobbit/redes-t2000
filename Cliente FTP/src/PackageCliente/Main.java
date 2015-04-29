package PackageCliente;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    private static int puerto;
    private static String direccion;
    private static BufferedReader lectura;
    private static boolean estado;
    private static Socket socket;
    private static PrintWriter escritura;
    private static BufferedReader entrada;

    public static void OPEN(String IP) throws Exception{
        direccion = IP;
        puerto = 21;

        try {
            socket = new Socket(direccion, puerto);
        }catch (Exception e){
            System.out.println("No se pudo conectar al servidor");
        }
        System.out.println("Conectado a " + direccion + " en el puerto " + Integer.toString(puerto));

        escritura = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String response = new String();
        response = entrada.readLine();
        System.out.println(response);

        if(response.contains("220")){
            while(true){
                System.out.print("Name(" + direccion + ":root): ");
                String mensaje = lectura.readLine();
                escritura.println(mensaje);
                escritura.flush();

                response = entrada.readLine();
                System.out.println(response);
                if(response.contains("331")){
                    System.out.print("Password: ");
                    mensaje = lectura.readLine();
                    escritura.println(mensaje);
                    escritura.flush();

                    response = entrada.readLine();
                    System.out.println(response);
                    if(response.contains("230")){
                        estado = true;
                        break;
                    }
                }
            }
        }
        if(!estado){
            QUIT();
        }
        //TransferirArchivo ftp = new TransferirArchivo(socket);
    }

    public static void CD(String comando){
        escritura.println(comando);
        escritura.flush();
        try {
            System.out.println(entrada.readLine());
        }catch (Exception e){}
    }

    public static void LS(String comando){
        try {
            escritura = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e){}

        escritura.println(comando);
        escritura.flush();

        ArrayList<String> Archivos = new ArrayList<>();
        String response = new String();
        try {
            response = entrada.readLine();
            System.out.println(response);
            if(response.contains("150")){
                while (true){
                    response = entrada.readLine();
                    if(response.contains("226")){
                        break;
                    }
                    else {
                        Archivos.add(response);
                        System.out.println(response);
                    }
                }
                System.out.println("\n" + response);
            }
        }catch (Exception e){
            System.out.println("Error: " + e);
        }

    }

    public static void GET(String dato){
    }

    public static void PUT(String dato){
    }

    public static void QUIT(){
        estado = false;
        System.out.println("Cerrando conexion...");
        try {
            socket.close();
        } catch (Exception e){
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Conexion finalizada");
        }

    }

    public static void main(String args[]) throws Exception
    {
        estado = false;
        String teclado = new String();
        System.out.println("Bienvenido al Cliente FTP");

        try {
            lectura = new BufferedReader(new InputStreamReader(System.in));
        }catch (Exception e){
            System.out.println(e);
        }
        OPEN("localhost"); //quitar esta linea
        while(true){
            System.out.print("> ");
            try {
                teclado = lectura.readLine().trim();
            }catch (Exception e){
                System.out.println(e);
            }

            if(teclado.contains("OPEN")) {
                OPEN(teclado);
            }
            else if(teclado.contains("CD")) {
                CD(teclado);
            }
            else if(teclado.contains("LS")) {
                LS(teclado);
            }
            else if(teclado.contains("GET")) {
                GET(teclado);
            }
            else if(teclado.contains("PUT")) {
                PUT(teclado);
            }
            else if(teclado.contains("QUIT")) {
                QUIT();
            }
            else {
                System.out.println("Comando desconocido.");
                System.out.println("Utilice: OPEN, CD, LS, GET, PUT, QUIT.");
            }
        }

        /*
        String direccion = "127.0.0.1";
        int puerto = 21;

        Socket socket = null;
        System.out.println("Conectando con " + direccion + " en el puerto " + Integer.toString(puerto));
        try {
            socket = new Socket(direccion, puerto);
        }catch (Exception e){
            System.out.println("No se pudo conectar al servidor");
            System.exit(0);
        }
        TransferirArchivo ftp = new TransferirArchivo(socket);
        ftp.displayMenu();
*/
    }
}
