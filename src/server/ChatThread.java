package server;

import server.Chat;

import java.io.*;
import java.net.Socket;

public class ChatThread extends Thread{
    Socket socket;
    Chat chat;
    int mojBroj;
    public ChatThread(Socket socket,Chat chat){
        this.socket = socket;
        this.chat = chat;
        mojBroj = this.chat.brKlijenata;
        this.chat.brKlijenata++;
    }
    @Override
    public void run() {
        super.run();
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            BufferedReader bw = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String poruka = "";
            while (true){
                if(chat.pokupiliPoruku==mojBroj){
                    pw.println(chat.novaPoruka);
                    chat.pokupiliPoruku++;
                }
                sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
