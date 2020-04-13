package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;

public class Server {
    static int port = 9000;
    static int brChatKlijenata = 0;
    static PrintWriter pw;
    static BufferedReader bw;
    static OutputStream os;
    static String zahtev;
    static String path = "src/server/DATA_SERVER";
    static Chat chat = new Chat();
    static HashMap<String, PodaciKlijenta> mapa = new HashMap<>();
    static int brojac = 0;
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);
            Socket s;
            while (true){
                s = ss.accept();
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
                bw = new BufferedReader(new InputStreamReader(s.getInputStream()));
                os = s.getOutputStream();
                zahtev = bw.readLine();
                System.out.println("STIGAO ZAHTEV: "+zahtev);
                String opcija = zahtev.split(":,,:")[0];
                if(opcija.equals("PALINDROM")){
                    ObradaPalindrom();
                }
                else if(opcija.equals("DATOTEKA_LISTA")){
                    DatotekaLista();
                }
                else if(opcija.equals("DATOEKA_PREUZIMANJE")){
                    DatotekaPreuzimanje();
                }
                else if(opcija.equals("ONLINE")){
                    VratiOnline();
                }
                else if(opcija.equals("ZAHTEV_CHAT")){
                    if(chat.brKlijenata<3){
                        RegistrujKlijenta(s);
                    }else{
                        PostaviPosmatranje(s);
                        pw.println("BROJ");
                    }
                    continue;
                }
                else if(opcija.equals("ODJAVA")){
                    Odjava();
                }
                else if(opcija.equals("PORUKA")){
                    ProslediPoruku();
                }
                else{
                    pw.println("NEISPRAVAN ZAHTEV");
                }
                pw.flush();
                pw.close();
                bw.close();
                os.close();
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void DatotekaLista(){
        File files = new File(path);
        String str = "";
        if(files.isDirectory()){
            for(File f:files.listFiles()){
                str+=f.getName()+"\t\t"+f.length()/1024+"KB:,,:";
            }
            if(str.equals("")){
                pw.println("NA SERVERU NEMA DATOTEKA");
            }else{
                pw.println(str.substring(0,str.length()-5));
                System.out.println("Vraceno nazad:");
                System.out.println(str.substring(0,str.length()-5));
            }
        }
    }
    private static void DatotekaPreuzimanje(){
        System.out.println("SERVER PRIMIO ZAHTEV ZA PREUZIMANJE");
        String filePath = path+"/"+zahtev.split(":,,:")[1];
        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            os.write(fileBytes);
            os.flush();
            System.out.println("Server zavrsio: "+filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void ObradaPalindrom(){
        System.out.println("SERVER PRIMIO ZAHTEV ZA PALINDROM");
        if(zahtev.split(":,,:").length<2){
            pw.println("NISTE UNELI STRING");
            System.out.println("NIJE UNET STRING");
            return;
        }
        String napred = zahtev.split(":,,:")[1].replaceAll(" ","");
        StringBuilder nazad = new StringBuilder(napred).reverse();
        if(napred.equals(nazad.toString())){
            pw.println("JESTE PALINDROM");
            System.out.println("JESTE PALINDROM");
        }
        else{
            pw.println("NIJE PALINDROM");
            System.out.println("NIJE PALINDROM");
        }
    }
    private static void ProslediPoruku(){
        chat.PosaljIPoruku(zahtev.split(":,,:")[1]);
        mapa.forEach( (key,value) -> {
            try {
                if(!key.startsWith("undefined")){
                    System.out.println(key);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(value.getSocket().getOutputStream())),true);
                    String salje = zahtev.split(":,,:")[1];
                    String poruka = zahtev.split(":,,:")[2];
                    pw.println(salje+" : "+poruka);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private static void RegistrujKlijenta(Socket socket){
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            String ime = zahtev.split(":,,:")[1];
            if(mapa.containsKey(ime)){
                pw.println("IME");
                PostaviPosmatranje(socket);
                return;
            }
            mapa.put(ime,new PodaciKlijenta(ime,++chat.brKlijenata,socket));
            System.out.println("Pridruzen"+ime+" "+chat.brKlijenata);
            pw.println("OK");
            String s = "OSVEZI:";
            for (String key:mapa.keySet()){
                if(!key.startsWith("undefined")){
                    s+=key+",";
                }
            }
            for (String key:mapa.keySet()){
                try {
                    PrintWriter pws = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mapa.get(key).getSocket().getOutputStream())),true);
                    pws.println(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void PostaviPosmatranje(Socket socket){
        String ime = "undefined"+brojac++;
        mapa.put(ime,new PodaciKlijenta(ime,++chat.brKlijenata,socket));
    }
    private static void VratiOnline(){
        String s = "";
        for (String key:mapa.keySet()){
            s+=key+":";
        }
        pw.println(s);
    }
    private static void Odjava(){
        String ime = zahtev.split(":,,:")[1];
        mapa.remove(ime);
        chat.brKlijenata--;
        String s = "OSVEZI:";
        for (String key:mapa.keySet()){
            if(!key.startsWith("undefined"))
            s+=key+",";
        }
        for (String key:mapa.keySet()){
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mapa.get(key).getSocket().getOutputStream())),true);
                pw.println(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
