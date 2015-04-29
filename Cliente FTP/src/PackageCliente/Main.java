package PackageCliente;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    static int puerto;
    static String direccion;
    static BufferedReader lectura;
    static boolean estado;
    static Socket socket;
    static PrintWriter escritura;
    static BufferedReader entrada;
    static DataInputStream flujoEntrada;
    static DataOutputStream flujoSalida;
    static String directorio;

    public static void OPEN(String IP) throws Exception{
        direccion = IP.split(" ")[1];
        try {
            socket = new Socket(direccion, 21);
        }catch (Exception e){
            System.out.println("No se pudo conectar al servidor");
        }
        System.out.println("Conectado a " + direccion + " en el puerto " + Integer.toString(21));

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

    public static void GET(String filename) throws Exception{
        String nombre = filename.split(" ")[1];

        File archivo = new File(directorio + nombre);
        if(archivo.exists()) {
            System.out.println("El archivo ya existe");
            return;
        }

        escritura.println("GET " + nombre);
        escritura.flush();
        String response = new String();
        response = entrada.readLine();

        if(!response.contains("150")) {
            System.out.println(response);
            return;
        }

        Socket socketData = null;
        try {
            socketData = new Socket(direccion, 20);
            flujoEntrada = new DataInputStream(socketData.getInputStream());
            flujoSalida = new DataOutputStream(socketData.getOutputStream());
        }catch (Exception e){
            System.out.println("No se pudo iniciar conexion de datos");
        }

        System.out.println(response);
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
            System.out.println(entrada.readLine());
        }catch (Exception e){
            System.out.println(e);
            return;
        } finally {
            socketData.close();
        }
    }

    public static void PUT(String filename) throws Exception {
        String nombre = filename.split(" ")[1];

        File archivo = new File(directorio + nombre);
        if(!archivo.exists()) {
            System.out.println("El archivo no existe");
            return;
        }

        escritura.println("PUT " + nombre);
        escritura.flush();

        String response = new String();
        response = entrada.readLine();
        if(!response.contains("350")) {
            System.out.println(response);
            return;
        }

        Socket socketData = null;
        try {
            socketData = new Socket(direccion, 20);
            flujoEntrada = new DataInputStream(socketData.getInputStream());
            flujoSalida = new DataOutputStream(socketData.getOutputStream());
        }catch (Exception e){
            System.out.println("No se pudo iniciar conexion de datos");
        }

        System.out.println("Enviando archivo ...");
        try {
            FileInputStream fin = new FileInputStream(archivo);
            int largo;
            do {
                largo = fin.read();
                flujoSalida.writeUTF(String.valueOf(largo));
            }
            while (largo != -1);
            fin.close();
            System.out.println(entrada.readLine());
        }catch (Exception e){
            System.out.println(e);
        }finally {
            socketData.close();
        }
    }

    public static void QUIT(){
        escritura.println("QUIT");
        escritura.flush();

        System.out.println("Cerrando conexion...");
        try {
            String response = entrada.readLine();
            System.out.println(response);
            if(response.contains("221")) {
                socket.close();
                System.out.println("Conexion finalizada");
                System.exit(0);
            }
            else{
                System.out.println(response);
            }

        } catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

    public static void main(String args[]) throws Exception
    {
        directorio = "Directorio_Cliente/";
        estado = false;
        String teclado = new String();
        System.out.println("Bienvenido al Cliente FTP");

        try {
            lectura = new BufferedReader(new InputStreamReader(System.in));
        }catch (Exception e){
            System.out.println(e);
        }

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
    }
}
