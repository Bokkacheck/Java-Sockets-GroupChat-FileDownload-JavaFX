package klijent;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class KlijentThread extends Thread {
    int port = 9000;
    String name = "localhost";
    Socket s;
    PrintWriter pw;
    BufferedReader br;
    BufferedReader brThread;
    volatile boolean enabled = true;
    public ListView lvPoruke;
    public ListView lvOnline;
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
    public String StartChat(String poruka,ListView lvPoruke,ListView lvOnline){
        try {
            this.lvPoruke = lvPoruke;
            this.lvOnline = lvOnline;
            InetAddress addr = InetAddress.getByName(name);
            s = new Socket(addr,9000);
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            brThread = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw.println(poruka);
            String odgovor = br.readLine();
            if(odgovor.equals("OK")){
                start();
                return "Uspesna konekcija";
            }else if(odgovor.equals("BROJ")){
                return "Maksimalni broj korisnika je vec konektovano, probajte kasnije";
            }
            else if(odgovor.equals("IME")){
                return "Odabrano ime je vec zauzeto";
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Doslo je do greske";
    }
    @Override
    public void run() {
        super.run();
        while (enabled){
            try {
                System.out.println("ceka");
                String poruka = brThread.readLine();
                System.out.println(poruka);
                if(poruka!=null){
                    System.out.println(poruka);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(poruka);
                            if(poruka.split(":")[0].equals("OSVEZI")){
                                lvOnline.getItems().clear();
                                System.out.println(poruka);
                                lvOnline.getItems().addAll(poruka.split(":")[1].split(","));
                                System.out.println(lvOnline.getItems().toString());
                            }
                            else{
                                lvPoruke.getItems().add(poruka);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void TurnOff(){
        enabled = false;
    }
}
