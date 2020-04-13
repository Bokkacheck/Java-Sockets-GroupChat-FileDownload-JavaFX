package klijent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class Klijent{
    int port = 9000;
    String name = "localhost";
    Socket s;
    PrintWriter pw;
    BufferedReader br;
    InputStreamReader isr;
    public String Server(String poruka){
        try {
            InetAddress addr = InetAddress.getByName(name);
            s = new Socket(addr,9000);
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw.println(poruka);
            String odgovor = br.readLine();
            pw.close();
            br.close();
            s.close();
            return odgovor;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "DOSLO JE DO GRESKE";
    }
    public String ServerPreuzmiDatoteku(String poruka,String datoteka,int velicina){
        try {
            File file = new File(datoteka);
            if(file.exists()){
                return "FAJL VEC POSTOJI";
            }
            InetAddress addr = InetAddress.getByName(name);
            s = new Socket(addr,9000);
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            pw.println(poruka);
            InputStream is = s.getInputStream();
            byte [] bytes = new byte[(velicina+1)*1024];
            int procitanoUkupno = 0;
            int procitano = 0;
            while (procitano>-1){
                procitano = is.read(bytes,procitanoUkupno,bytes.length-procitanoUkupno);
                if(procitano>0){
                    procitanoUkupno+=procitano;
                }
            }
            System.out.println("Greska je: "+(bytes.length-procitanoUkupno));
            Files.createFile(file.toPath());
            FileOutputStream fos = new FileOutputStream(datoteka);
            fos.write(bytes,0,procitanoUkupno);
            fos.flush();
            fos.close();
            is.close();
            pw.close();
            s.close();
            return "Fajl "+datoteka+" uspesno preuzet";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "DOSLO JE DO GRESKE";
    }
}
