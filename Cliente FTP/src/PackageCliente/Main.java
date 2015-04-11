package PackageCliente;

import java.io.*;
import java.net.*;

public class Main {
    private static int puerto = 21;

    public static void OPEN(){
        try {
            Socket socket = new Socket("127.0.0.1", puerto);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write("USER " + "yorsh21" + "\r\n");
            bufferedWriter.flush();

            String response = new String();
            while ((response = bufferedReader.readLine()) != null) {
                System.out.println(response);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void OPEN2(){
        try{
            Socket socket = new Socket("127.0.0.1", 21);
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = new String();
            while((response = entrada.readLine()) != null)
                System.out.println(response);
            salida.flush();

            /*
            BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt")));

            while((s = entrada.readLine()) != null)
                pw.write(s);
            pw.close();

            if(entrada.readLine() == null)
                System.out.println("Archivo recibido OK");
             */

            socket.close();


        }catch (Exception e){
            System.out.println("Error: " + e);
        }
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

        OPEN2();
    }
}
