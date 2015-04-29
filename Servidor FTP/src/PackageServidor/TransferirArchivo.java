package PackageServidor;

import java.net.*;
import java.io.*;
import java.util.*;

public class TransferirArchivo  extends Thread
{
    Socket ClientSoc;

    DataInputStream entrada;
    DataOutputStream salida;

    TransferirArchivo(Socket socket) {
        try {
            ClientSoc = socket;
            entrada = new DataInputStream(ClientSoc.getInputStream());
            salida = new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();
        }
        catch(Exception ex) {
        }
    }

    void SendFile() throws Exception {
        String filename = entrada.readUTF();
        File f=new File(filename);
        if(!f.exists())
        {
            salida.writeUTF("File Not Found");
            return;
        }
        else
        {
            salida.writeUTF("READY");
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                salida.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);
            fin.close();
            salida.writeUTF("File Receive Successfully");
        }
    }

    void ReceiveFile() throws Exception {
        String filename = entrada.readUTF();
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;

        if(f.exists())
        {
            salida.writeUTF("File Already Exists");
            option = entrada.readUTF();
        }
        else
        {
            salida.writeUTF("SendFile");
            option="Y";
        }

        if(option.compareTo("Y")==0)
        {
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
            do
            {
                temp = entrada.readUTF();
                ch=Integer.parseInt(temp);
                if(ch!=-1)
                {
                    fout.write(ch);
                }
            }while(ch!=-1);
            fout.close();
            salida.writeUTF("File Send Successfully");
        }
        else
        {
            return;
        }

    }


    public void run() {
        while(true) {
            try {
                System.out.println("Waiting for Command ...");
                String Command = entrada.readUTF();
                if(Command.compareTo("GET")==0) {
                    System.out.println("\tGET Command Received ...");
                    SendFile();
                    continue;
                }
                else if(Command.compareTo("SEND")==0) {
                    System.out.println("\tSEND Command Receiced ...");
                    ReceiveFile();
                    continue;
                }
                else if(Command.compareTo("DISCONNECT")==0) {
                    System.out.println("\tDisconnect Command Received ...");
                    System.exit(1);
                }
            }
            catch(Exception ex)
            {
            }
        }
    }
}
