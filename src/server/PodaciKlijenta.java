package server;

import java.net.Socket;

public class PodaciKlijenta {
    private String ime;
    private int broj;
    private Socket socket;

    public PodaciKlijenta(String ime, int broj, Socket socket) {
        this.ime = ime;
        this.broj = broj;
        this.socket = socket;
    }

    public String getIme() {
        return ime;
    }
    public void setIme(String ime) {
        this.ime = ime;
    }
    public int getBroj() {
        return broj;
    }
    public void setBroj(int broj) {
        this.broj = broj;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
