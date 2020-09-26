import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Fysikk extends AnimationTimer {

    Ball[] baller;
    Stage stage;
    double g;
    double loss;
    FysikkInnstillinger fysikkInnstillinger;

    public Fysikk(Stage s) {
        stage = s;
        g = 1;
        loss = 0.9;
        fysikkInnstillinger = new FysikkInnstillinger();
    }

    public FysikkInnstillinger hentInstillinger() {
        return fysikkInnstillinger;
    }

    public void setG(double gra) {
        g = gra;
    }

    public void setLoss(double l) {
        loss = l;
    }

    public void setBaller(Ball[] b) {
        //baller = new Ball[b.length - 1];
        baller = b;
    }

    @Override
    public void handle(long now) {
        for (Ball ball : baller) {
            kollisjon(ball);
        }
        for (Ball ball : baller) {
            if (ball.bevege() == true) {
                akselrasjon(ball);
                vegg(ball);
                ball.pos[0] += ball.fart[0];
                ball.pos[1] += ball.fart[1];
                
                ball.circle.setCenterY(ball.pos[0]);
                ball.circle.setCenterX(ball.pos[1]);
            }
        }
    }

    private void akselrasjon(Ball ball) {
        if (ball.fart[0] > -0.1 && ball.fart[0] < 0.1 && ball.pos[0] > (stage.getHeight() - 22 - 40)) {
            ball.fart[1] *= 0.99;
            ball.fart[0] = 0;
            ball.pos[0] = ((stage.getHeight() - 22) - ball.hentCircle().getRadius());
        } else if (ball.pos[0] >= ((stage.getHeight() - 22) - ball.hentCircle().getRadius()) - ball.fart[0]) {
            ball.pos[0] = (stage.getHeight() - 22 - ball.hentCircle().getRadius());
            ball.fart[0] *= -loss;

        } else {
            ball.fart[0] += g;
        }
    }

    private void vegg(Ball ball) {
        if (ball.pos[1] < 0 + ball.hentCircle().getRadius()) {
            ball.pos[1] = ball.hentCircle().getRadius() + 2;
            ball.fart[1] *= -0.8;
        } else if (ball.pos[1] > stage.getWidth() - ball.hentCircle().getRadius()) {
            ball.pos[1] = stage.getWidth() - (ball.hentCircle().getRadius() + 2);
            ball.fart[1] *= -0.8;
        }
    }

    private void kollisjon(Ball ball) {
        for (Ball b : baller) {
            if (b != ball && ball.hentCircle().intersects(b.hentCircle().getBoundsInLocal())) {
                ball.fart[1] *= -1;
                ball.fart[0] *= -1;
            }
        }
    }

    protected class FysikkInnstillinger implements InnstillingerInterface {
        Label gravLabel;
        Slider gravSlider;
        String text;
        Slider lossSlider;
        Label lossLabel;
        Task<Void> task;
        Thread t1;
        VBox layout;

        protected FysikkInnstillinger() {
            setUp();
        }


        public String hentNavn() {
            return "Fysikk";
        }

        private void setUp() {
            gravSlider = new Slider(0, 5, 1);
            gravSlider.setShowTickMarks(true);
            gravSlider.setShowTickLabels(true);
            gravSlider.setMajorTickUnit(0.5);
            gravSlider.setBlockIncrement(0.5);
            gravSlider.setSnapToTicks(true);
            gravSlider.setValue(g);
    
            lossSlider = new Slider(0.1, 1, 0.9);
            lossSlider.setShowTickMarks(true);
            lossSlider.setShowTickLabels(true);
            lossSlider.setMajorTickUnit(0.25);
            lossSlider.setBlockIncrement(0.1);
            lossSlider.setSnapToTicks(false);
            lossSlider.setValue(loss);
    
            gravLabel = new Label();
            lossLabel = new Label();
            task = new Task<Void>() {
    
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        Platform.runLater(new Runnable(){    
                            @Override
                            public void run() {
                                if (!gravSlider.isValueChanging()) {
                                    gravLabel.setText("Gravitasjon: " + String.valueOf(gravSlider.getValue()));
                                }
                                if (!lossSlider.isValueChanging()) {
                                    lossLabel.setText("Energitap: " + String.valueOf(lossSlider.getValue()));
                                }
                            }
                        });
                        Thread.sleep(500);
                    }
                } 
            };
    
            t1 = new Thread(task);
            t1.setDaemon(true);
            t1.start();

            layout = new VBox(10);
            layout.getChildren().addAll(gravLabel, gravSlider,lossLabel, lossSlider);
            layout.setAlignment(Pos.CENTER);
        }

        public VBox hentLayout() {
            
            return layout;
        }

        public void startDen() {
            if (baller != null) {
                start();
            }
        }

        public  void save() {
            double g = gravSlider.getValue();
            setG(g);
            setLoss(lossSlider.getValue());
            startDen();
        }

    }
}