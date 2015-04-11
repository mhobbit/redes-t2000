package PackageServidor;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Main {
    public int entero = 1;

    public void OPEN(){

    }

    public void CD(){

    }

    public void LS(){

    }

    public void GET(){

    }

    public void PUT(){

    }

    public void QUIT(){

    }



    public static void main(String arg[]){

        try{
            ServerSocket servidor = new ServerSocket(21);

            System.out.println("Servidor Activo...");
            Socket socket = servidor.accept();
            System.out.println("Cliente Entrante...");

            PrintWriter escritura = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader lectura = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /*
            String rutaArchivo = "C:\\Users\\JorgeAndrés\\Desktop\\Captura.JPG";
            File archivo = new File(rutaArchivo);
            FileReader bufferArchivo = new FileReader(archivo);
            */

            escritura.println(" Hola Soy el Servidor");
            escritura.println("aoooalsdas");
            escritura.flush();

            /*
            String temporal = lectura.readLine();
            int num = Integer.parseInt(temporal)-48;
            System.out.println("index: " + num);
            */

            //socket.close();
            //servidor.close();


        }catch (Exception e){}

    }
}