package klijent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Prikaz3 extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    Stage stage;
    Button prviZadatak = new Button("Prvi zadatak");
    Button drugiZadatak = new Button("Drugi zadatak");
    Button treciZadatak = new Button("Treci zadatak");
    Scene pocetnaScena;
    String ime = "";
    KlijentThread klijent = new KlijentThread();
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        PocetnaScena();
        Zadatak1();
        Zadatak2();
        Zadatak3();
        stage.setScene(pocetnaScena);
        stage.setTitle("OP2 - Bojan Stojkovic NRT-4/17");
        stage.show();
    }
    private void PocetnaScena(){
        HBox pane = new HBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().add(prviZadatak);
        pane.getChildren().add(drugiZadatak);
        pane.getChildren().add(treciZadatak);
        pocetnaScena = new Scene(pane,600,600);
    }
    private void Zadatak1(){
        BorderPane pane = new BorderPane();
        Button vratiSeNazad = new Button("Nazad");
        pane.setTop(vratiSeNazad);
        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(50));
        vBox.setAlignment(Pos.CENTER);
        Label lblText = new Label("Unesite string za proveru:");
        Label lblIspis = new Label("");
        TextField txtUnos = new TextField();
        Button btnProveri = new Button("ISPITAJ");
        btnProveri.setOnAction(event -> {
            lblIspis.setText(klijent.Server("PALINDROM:,,:"+txtUnos.getText()));
        });
        vBox.getChildren().addAll(lblText,txtUnos,btnProveri,lblIspis);
        pane.setCenter(vBox);
        vratiSeNazad.setOnAction(event -> {stage.setScene(pocetnaScena);});
        TextField txt = new TextField();
        Scene zadatak1  = new Scene(pane,600,600);
        prviZadatak.setOnAction(event -> {
            stage.setScene(zadatak1);
        });
    }
    private void Zadatak2(){
        BorderPane pane = new BorderPane();
        Button vratiSeNazad = new Button("Nazad");
        vratiSeNazad.setOnAction(event -> {stage.setScene(pocetnaScena);});
        pane.setTop(vratiSeNazad);
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        pane.setCenter(vBox);
        Label lblText = new Label("Dostupne datoteke");
        ListView lvDatoteke = new ListView();
        Button btnRefresh = new Button("OSVEZI");
        Button btnPreuzmi = new Button("PREUZMI DATOTEKU");
        Label lblPoruka = new Label("");
        vBox.getChildren().addAll(lblText,lvDatoteke,btnRefresh,btnPreuzmi,lblPoruka);
        Scene zadatak2  = new Scene(pane,600,600);
        btnRefresh.setOnAction(event -> OsveziDatoteke(lvDatoteke,lblPoruka));
        drugiZadatak.setOnAction(event -> {
            stage.setScene(zadatak2);
            OsveziDatoteke(lvDatoteke,lblPoruka);
        });
        btnPreuzmi.setOnAction(event -> {
            PreuzmiDatoteku(lvDatoteke,lblPoruka);
        });
    }
    private void Zadatak3(){
        BorderPane pane = new BorderPane();
        Button vratiSeNazad = new Button("Nazad");
        pane.setTop(vratiSeNazad);
        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(20));
        HBox hBox = new HBox(10);
        VBox prvi = new VBox(10);
        VBox drugi = new VBox(10);
        drugi.setPrefHeight(120);
        Label lblIme = new Label("Unesite vase ime: ");
        Button btnIme = new Button("Potvrdi");
        Label lblOnline = new Label("Online korisnici");
        TextField txtIme = new TextField();
        Label lblOdgovor = new Label("");
        prvi.getChildren().addAll(lblIme,txtIme,btnIme,lblOdgovor);
        ListView lvOnline = new ListView();
        drugi.getChildren().addAll(lblOnline,lvOnline);
        hBox.getChildren().addAll(prvi,drugi);
        ListView lvPoruke = new ListView();
        TextField txtPoruka = new TextField();
        Button btnPosalji = new Button("Posalji");
        vBox.getChildren().add(hBox);
        pane.setCenter(vBox);
        Scene zadatak3 = new Scene(pane,600,600);
        treciZadatak.setOnAction(event -> {
            klijent = new KlijentThread();
            lvOnline.getItems().clear();
            lvOnline.getItems().addAll(klijent.Server("ONLINE:,,:").split(":"));
            txtIme.setDisable(false);
            btnIme.setDisable(false);
            stage.setScene(zadatak3);
        });
        btnPosalji.setOnAction(event -> {
            klijent.Server("PORUKA:,,:"+ime+":,,:"+txtPoruka.getText());
        });
        btnIme.setOnAction(event -> {
            ime = txtIme.getText();
            String odgovorServera = klijent.StartChat("ZAHTEV_CHAT:,,:"+ime+":,,:",lvPoruke,lvOnline);
            if(odgovorServera.equals("Uspesna konekcija")){
                txtIme.setDisable(true);
                btnIme.setDisable(true);
                vBox.getChildren().addAll(lvPoruke,txtPoruka,btnPosalji);
                lblOdgovor.setText(odgovorServera);
            }else{
                lblOdgovor.setText(odgovorServera);
            }
            lvOnline.getItems().clear();
            lvOnline.getItems().addAll(klijent.Server("ONLINE:,,:").split(":"));
        });
        vratiSeNazad.setOnAction(event -> {
            klijent.Server("ODJAVA:,,:"+ime);
            klijent.TurnOff();
            vBox.getChildren().removeAll(lvPoruke,txtPoruka,btnPosalji);
            stage.setScene(pocetnaScena);
        });
        stage.setOnCloseRequest( event -> {
            if(stage.getScene().equals(zadatak3) && !btnIme.isDisable()){        //Znaci da je konektovan na server za chat
                klijent.Server("ODJAVA:,,:"+ime);
                klijent.TurnOff();
            }
            Platform.exit();
        } );

    }
    private void PreuzmiDatoteku(ListView lv,Label lbl){
        if(lv.getSelectionModel().getSelectedItem()==null){
            lbl.setText("NIJE SELEKTOVANA DATOTEKA");
            return;
        }
        String odabrano = lv.getSelectionModel().getSelectedItem().toString();
        if(odabrano.contains("\t\t")){
            String datoteka = odabrano.split("\t\t")[0];
            int velicina = Integer.parseInt(odabrano.split("\t\t")[1].substring(0,odabrano.split("\t\t")[1].length()-2));
            lbl.setText(klijent.ServerPreuzmiDatoteku("DATOEKA_PREUZIMANJE:,,:"+datoteka,datoteka,velicina));
        }else{
            lbl.setText("NIJE SELEKTOVANA DATOTEKA");
        }
    }
    private void OsveziDatoteke(ListView lv,Label lbl){
        lv.getItems().clear();
        String[] datoteke = klijent.Server("DATOTEKA_LISTA:,,:").split(":,,:");
        if(datoteke.length!=0){
            lv.getItems().addAll(datoteke);
        }else{
            lv.getItems().add("NEMA DATOTEKA NA SERVERU");
        }
        lbl.setText("");
    }
}
