import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Hovedprogram extends Application {

    //Tar inn en vei til aapning, returnerer et boolean dobbeltarray som viser veien i labyrinten
    static boolean[][] losningStringTilTabell2(String losningString, int hoyde, int bredde) {
        boolean[][] losning = new boolean[hoyde][bredde];
        if (!losningString.equals("ingen utvei") || losningString == null) {
            String[] x = losningString.split(" \\) --> \\( ");
            x[0] = x[0].substring(2);
            x[x.length - 1] = x[x.length - 1].substring(0, x[x.length - 1].length() - 2);
            
            for (int n = 0; n< hoyde; n++) {
                for (int k = 0; k< bredde; k++) {
                    losning[n][k] = false;
                }
            }
            for (String s : x) {
                String[] kor = s.split(",");
                losning[Integer.parseInt(kor[0])][Integer.parseInt(kor[1])] = true;
            }

            return losning;

        } else {
            for (int n = 0; n< hoyde; n++) {
                for (int k = 0; k< bredde; k++) {
                    losning[n][k] = false;
                }
            }
            return losning;
        }
    }


    //En ekstra klasse som arver fra Rectangle
    //Lagrer i tillegg den tilsvarende ruten fra labyrinten
    private class Rect extends Rectangle {
        Rute rute;
        public Rect(double x, double y, Color c, Rute r) {
            super(x, y, c);
            rute = r;
        }

        public Rute hentRute() {
            return rute;
        }
    }

    Stage stage;
    Scene scene;
    Labyrint lab;
    VBox kulisser;
    Rect[][] liste;
    int veiNr = 0;
    Lenkeliste<String> listeString;
    Text text;
    FileChooser filvelger;

    //Begynnter programmet
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        kulisser = new VBox(10);
        kulisser.setAlignment(Pos.CENTER);
        scene = new Scene(kulisser, 650, 650);
        stage.setScene(scene);

        //Lager en filvelger og setter på filtere
        filvelger = new FileChooser();
        filvelger.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Labyrinter", "*.in"));

        //Lager en knapp som kaller på filvelgeren og tegner en labyrint
        Button filvelgerKnapp = new Button("velg fil");
        filvelgerKnapp.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
                try {
                    lab = Labyrint.lesFraFil(filvelger.showOpenDialog(null));
                    text.setText("Velg en rute");

                    listeString = new Lenkeliste<String>();

                    kulisser.getChildren().remove(1);
                    kulisser.getChildren().add(1, lagLab());
                } catch (FileNotFoundException f) {
                    System.out.println(f);
                }
                	
			}
        });

        kulisser.getChildren().addAll(filvelgerKnapp, new GridPane());

        //Knapp for å se neste utvei fra kordinatet som er bestemt
        HBox bokser = new HBox(20);
        bokser.setAlignment(Pos.CENTER);
        Button neste = new Button("Neste");
        neste.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (veiNr == listeString.stoerrelse() - 1) {
                    veiNr = 0;
                } else {
                    veiNr++; 
                }  
                visUtvei(visNr(veiNr)); 
            }  
        });

        //Lager en knapp for å se forrige utvei 
        Button forrige = new Button("forrige");
        forrige.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (veiNr == 0) {
                    veiNr = listeString.stoerrelse() - 1;
                } else {
                    veiNr--; 
                }   
                visUtvei(visNr(veiNr));
                
            }  
        });

        bokser.getChildren().addAll(forrige, neste);

        //Lager en knapp som viser den korteste ruten fra korditaten som er valgt
        Button korteste = new Button("Korteste vei");
        korteste.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                
                visUtvei(korteste());
                text.setText("Dette er den korteste veien!");
            }
        });

        //Viser en tilfeldig vei av de veiene som mulige. 
        Button tilfeldig = new Button("Tilfeldig vei");
        tilfeldig.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (!text.getText().equals("Ingen utvei") && !text.getText().equals("Velg en rute")) {
                    System.out.println(text.getText());
                    veiNr = (int)(Math.random()*listeString.stoerrelse());
                    visUtvei(visNr(veiNr));
                } 
            }
        });

        HBox knapper = new HBox(20);
        knapper.setAlignment(Pos.CENTER);
        knapper.getChildren().addAll(tilfeldig, korteste);

        text = new Text();
        kulisser.getChildren().addAll(text, bokser, knapper);

        stage.show();
    }

    //Lager GridPane med hele labyrinten som er valgt.
    public GridPane lagLab() {
        GridPane grid = new GridPane();
        grid.setLayoutY(lab.hentKol());
        grid.setLayoutX(lab.hentRad());
        grid.setGridLinesVisible(false);
        grid.setAlignment(Pos.CENTER);

        //Justerer stoerrelsen til rutene
        int bredde = (550 / lab.hentKol());
        int hoyde = (420 / lab.hentRad());
        int str;
        if (bredde > hoyde) {
            str = hoyde;
        } else {
            str = bredde;
        }

        liste = new Rect[lab.hentRad()][lab.hentKol()];

        //Legger til ruter i labyrinten
        for (int n = 0; n< lab.hentRad(); n++) {
            for (int k = 0; k< lab.hentKol(); k++) {
                Rect rect = new Rect(str, str, lab.hentRuter()[n][k].hentFarge(), lab.hentRuter()[n][k]);
                rect.setStroke(Color.web("black", 0.5));
                rect.setStrokeWidth(0.5);
                rect.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        listeString = lab.finnUtveiFra(rect.hentRute().rad, rect.hentRute().kol);
                        veiNr = 0;
                        visUtvei(visNr(veiNr));
                    }                  
                });

                liste[n][k] = rect;
                grid.add(rect, k, n);              
            }
        }

        return grid;
    }

    //returnerer veien til aapningen som er valgt ut av indeks
    private String visNr(int n) {
        try {
            if (listeString.stoerrelse() != 0) {
                return listeString.hent(n);
            } else {
                return "Ingen utvei";
            }
        } catch(NullPointerException e) {
            return "Ingen utvei";
        }
    }

    //returnerer den korteste veien ut
    private String korteste() {
        if (listeString.stoerrelse() != 0) {
            String kortRute = listeString.hent(0);
            for (String vei : listeString) {
                if (vei.split("-->").length < kortRute.split("-->").length) {
                    kortRute = vei;
                }
            }
            return kortRute;
        } else {
            return "Ingen utvei";
        }
    }

    //endrer teksten med informasjon og viser utveien som er valgt
    public void visUtvei(String s) {
        String vei = s;
        if (listeString.stoerrelse() != 0) {
            text.setText("Viser utvei " + String.valueOf(veiNr + 1) + " av " + listeString.stoerrelse());
            boolean[][] x = losningStringTilTabell2(vei, lab.hentRad(), lab.hentKol());

            for (int n = 0; n< lab.hentRad(); n++) {
                for (int k = 0; k< lab.hentKol(); k++) {
    
                    if (x[n][k]) {
                        liste[n][k].setFill(Color.RED);
                    } else {
                        liste[n][k].setFill(liste[n][k].hentRute().hentFarge());
                    }
                }
            }
        } else {
            text.setText("Ingen utvei");
            for (int n = 0; n< lab.hentRad(); n++) {
                for (int k = 0; k< lab.hentKol(); k++) {
                    liste[n][k].setFill(liste[n][k].hentRute().hentFarge());
                }
            }
        }
    }   
}