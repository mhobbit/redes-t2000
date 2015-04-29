package PackageCliente;

import java.net.*;
import java.io.*;
import java.util.*;

public class TransferirArchivo
{
    Socket ClienteSoccket;

    DataInputStream entrada;
    DataOutputStream salida;
    BufferedReader lectura;
    TransferirArchivo(Socket socket) {
        try
        {
            ClienteSoccket = socket;
            entrada = new DataInputStream(ClienteSoccket.getInputStream());
            salida = new DataOutputStream(ClienteSoccket.getOutputStream());
            lectura = new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex) {
        }
    }
    void SendFile() throws Exception {

        String filename;
        System.out.print("Enter File Name :");
        filename = lectura.readLine();

        File f = new File(filename);
        if(!f.exists())
        {
            System.out.println("File not Exists...");
            salida.writeUTF("File not found");
            return;
        }

        salida.writeUTF(filename);

        String msgFromServer = entrada.readUTF();
        if(msgFromServer.compareTo("File Already Exists") == 0)
        {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            Option = lectura.readLine();
            if(Option == "Y")
            {
                salida.writeUTF("Y");
            }
            else
            {
                salida.writeUTF("N");
                return;
            }
        }

        System.out.println("Sending File ...");
        FileInputStream fin = new FileInputStream(f);
        int ch;
        do
        {
            ch = fin.read();
            salida.writeUTF(String.valueOf(ch));
        }
        while(ch != -1);
        fin.close();
        System.out.println(entrada.readUTF());

    }

    void ReceiveFile() throws Exception {
        String fileName;
        System.out.print("Enter File Name :");
        fileName=lectura.readLine();
        salida.writeUTF(fileName);
        String msgFromServer=entrada.readUTF();

        if(msgFromServer.compareTo("File Not Found")==0)
        {
            System.out.println("File not found on Server ...");
            return;
        }
        else if(msgFromServer.compareTo("READY")==0)
        {
            System.out.println("Receiving File ...");
            File f=new File(fileName);
            if(f.exists())
            {
                String Option;
                System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
                Option=lectura.readLine();
                if(Option=="N")
                {
                    salida.flush();
                    return;
                }
            }
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
            do
            {
                temp=entrada.readUTF();
                ch=Integer.parseInt(temp);
                if(ch!=-1)
                {
                    fout.write(ch);
                }
            }while(ch!=-1);
            fout.close();
            System.out.println(entrada.readUTF());

        }


    }
}
