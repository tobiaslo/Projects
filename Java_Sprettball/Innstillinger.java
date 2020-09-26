import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Innstillinger {
    Stage stage;
    InnstillingerInterface fysikkInnstillinger;
    InnstillingerInterface genereltInnstillinger;
    Button save;
    Scene main;
    VBox layout;
    Button fysButton;
    Button antallButton;


    public Innstillinger(Fysikk f, InnstillingerInterface i) {
        stage = new Stage();
        fysikkInnstillinger = f.hentInstillinger();
        genereltInnstillinger = i;
        setUp();
        
    }

    public void setUp() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Innstillinger");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                fysikkInnstillinger.startDen();
            }  
        });

        fysButton = new Button(fysikkInnstillinger.hentNavn());
        fysButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                layout.getChildren().remove(1);
                layout.getChildren().add(1, fysikkInnstillinger.hentLayout());
            }    
        });

        antallButton = new Button(genereltInnstillinger.hentNavn());
        antallButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                layout.getChildren().remove(1);
                layout.getChildren().add(1, genereltInnstillinger.hentLayout());
            }  
        });

        save = new Button("Lagre");
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                genereltInnstillinger.save();
                fysikkInnstillinger.save();
                lukk();

            }
            
        });
        HBox faner = new HBox(10);
        faner.getChildren().addAll(fysButton, antallButton);
        faner.setAlignment(Pos.CENTER);
        layout = new VBox(20);
        main = new Scene(layout, 250, 300, Color.BLACK);
        layout.getChildren().addAll(faner, fysikkInnstillinger.hentLayout(), save);
        layout.setAlignment(Pos.CENTER);
        setScene(main);
    }

    public void setScene(Scene s) {
        stage.setScene(s);
    }

    public void startInnstilling() {
        stage.showAndWait();
    }

    public void lukk() {
        stage.hide(); 
    }
}