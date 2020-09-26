import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SprettballV3 extends Application {

    Stage stage;
    Button button;
    FargeVelger fargevelger;
    double x = 300;
    double y = 300;
    double fartY = 0;
    double fartX = 0;
    double loss = 0.85;
    int antall = 1;
    Innstillinger innstillinger;
    Fysikk fysikk;
    Pane kulisser;
    Group gruppeBaller;
    int r = 30;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        gruppeBaller = new Group();
        fysikk = new Fysikk(stage);
        nyBall(Color.BLACK);

        innstillinger = new Innstillinger(fysikk, new SprettballInnstillinger());

        button = new Button();
        button.setText("Innstillinger");
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                fysikk.stop();
                innstillinger.startInnstilling();
                ;
            }
        });

        kulisser = new Pane();
        Scene scene = new Scene(kulisser, 600, 600, Color.BLACK);
        stage.setScene(scene);

        kulisser.getChildren().addAll(button, gruppeBaller);
        stage.show();
        innstillinger.startInnstilling();
    }

    private Ball[] nyBall(Paint p) {
        gruppeBaller.getChildren().clear();
        if (antall == 0) {
            fysikk.setBaller(null);
            return null;
        } else {
            Ball[] baller1 = new Ball[antall];
            for (int i = 0; i < antall; i++) {
                Ball ball = new Ball(Math.random() * 500, Math.random() * 600, r, new double[] { Math.random() * 5, Math.random() * 10 }, p);
                gruppeBaller.getChildren().add(ball.hentCircle());
                baller1[i] = ball;
            }
            fysikk.setBaller(baller1);
            return baller1;
        }
    }

    private class SprettballInnstillinger implements InnstillingerInterface {

        VBox layout;
        TextField antallText;
        TextField radiusText;
        Button farger;

        private SprettballInnstillinger() {
            setUp();
        }

        private void setUp() {
            antallText = new TextField(String.valueOf(antall));
            antallText.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
                    if (event.getCode() != KeyCode.BACK_SPACE) {
                        try {
                            Integer.parseInt(antallText.getText());
                        } catch (NumberFormatException n) {
                            antallText.setText(null);
                        }
                    }
				}
            });

            radiusText = new TextField(String.valueOf(r));
            radiusText.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
                    if (event.getCode() != KeyCode.BACK_SPACE) {
                        try {
                            Integer.parseInt(radiusText.getText());
                        } catch (NumberFormatException n) {
                            radiusText.setText(null);
                        }
                    }
				}
            });

            farger = new Button("Farge");
            fargevelger = new FargeVelger();
            farger.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    fargevelger.start();
                }
            });


            layout = new VBox(10);
            layout.getChildren().addAll(new Label("Antall sprettballer:"), antallText, new Label("Radius:"), radiusText, farger);
            layout.setAlignment(Pos.CENTER);
            
        }

        @Override
        public VBox hentLayout() {
            return layout;
        }

        @Override
        public String hentNavn() {
            return "Generelt";
        }

        @Override
        public void save() {
            try {
                r = Integer.parseInt(radiusText.getText());
            } catch (NumberFormatException n) {
                System.out.println(n);
            }

            try {
                 antall = Integer.parseInt(antallText.getText());
                
            } catch (NumberFormatException n) {
                System.out.println(n);
            }
            
            nyBall(fargevelger.getColor());
            radiusText.setText(String.valueOf(r));
            antallText.setText(String.valueOf(antall));
        }

        @Override
        public void startDen() {

        }
        
    }
}