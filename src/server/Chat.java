package server;

public class Chat {
    public volatile int brKlijenata;
    public volatile String staraPoruka;
    public volatile String novaPoruka;
    public volatile int pokupiliPoruku;
    public Chat(){
        pokupiliPoruku = 0;
        brKlijenata = 0;
        novaPoruka = "";
        staraPoruka = "";
    }
    public void PridruziSe(){
        brKlijenata++;
    }
    public void PosaljIPoruku(String poruka){
        novaPoruka = poruka;
        pokupiliPoruku = 0;
    }
    public synchronized void StiglaPoruka(){
        try {
            while (novaPoruka.equals(staraPoruka)){
                wait();
            }
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        staraPoruka = novaPoruka;
    }
}
