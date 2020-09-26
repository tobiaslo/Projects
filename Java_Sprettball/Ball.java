import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Ball {
    Circle circle;
    double[] pos = new double[2];
    double[] fart;
    long startTid;
    boolean bevege;
    boolean drag;

    public Ball(double posY, double posX, int r, double[] f, Paint p) {
        pos[0] = posY;
        pos[1] = posX;
        fart = f;
        circle = new Circle(pos[0], pos[1], r, p);
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setStroke(Color.web("black", 0.9));
        circle.setStrokeWidth(3);
        setUp();
        bevege = true;
        drag = false;
    }

    private void startBevege() {
        bevege = true;
    }

    private void stopBevege() {
        bevege = false;
    }

    public boolean bevege() {
        return bevege;
    }

    public Circle hentCircle() {
        return circle;
    }

    private void setUp() {
        double[] startPos = new double[2];
        
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {

            double bevegeX = circle.getCenterX();
            double bevegeY = circle.getCenterY();
            long tid = System.currentTimeMillis();
            double[][] fartHistorie = {{0,0}, {0,0}, {0,0}, {0,0}};

            private double[] utregning2(MouseEvent event) {
                double fartY = ((event.getSceneY()-bevegeY)*8) / (System.currentTimeMillis() - tid);
                double fartX = ((event.getSceneX()-bevegeX)*6) / (System.currentTimeMillis() - tid);
                double[] fart2 = {fartY, fartX};

                bevegeX = circle.getCenterX();
                bevegeY = circle.getCenterY();
                tid = System.currentTimeMillis();

                fartHistorie[3] = fartHistorie[2];
                fartHistorie[2] = fartHistorie[1];
                fartHistorie[1] = fartHistorie[0];
                fartHistorie[0] = fart2;

                double[] gjennomsnitt = {0, 0};
                for (double[] d : fartHistorie) {
                    gjennomsnitt[0] += d[0];
                    gjennomsnitt[1] += d[1];
                }

                gjennomsnitt[0] = gjennomsnitt[0] / 4;
                gjennomsnitt[1] = gjennomsnitt[1] / 4;

                return gjennomsnitt;
            }


            @Override
            public void handle(MouseEvent event) {
                drag = true;
                pos[0] = event.getSceneY();
                pos[1] = event.getSceneX();
                circle.setCenterY(pos[0]);
                circle.setCenterX(pos[1]);

                double[] hastighet = utregning2(event);
                fart = hastighet;
            } 
        });

        circle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drag = false;
                stopBevege();
                startPos[0] = event.getSceneY();
                startPos[1] = event.getSceneX();
                startTid = System.currentTimeMillis();
            }
        });

        circle.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                startBevege();

                if (!drag) {
                    Info in = new Info();
                    in.start();
                }

                drag = false;

            }           
        });

    }

    private class Info {

        private void start() {
            Stage stage = new Stage();
            stage.setAlwaysOnTop(true);
            stage.setTitle("Informasjon");

            VBox vbox = new VBox(10);

            Graf fartGraf = new Graf("fart", 60);

            Button visGraf = new Button("Vis graf");
            visGraf.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    
                    Stage grafStage = new Stage();
                    grafStage.setAlwaysOnTop(true);
                    grafStage.setScene(fartGraf.getScene());
                    grafStage.show();
                }  
            });

            Label fartLabel = new Label();
            
            Label posisjonLabel = new Label();

            Label radiusLabel = new Label("Radius: " + circle.getRadius());

            Slider radius = new Slider(1, 100, circle.getRadius());
            radius.setShowTickMarks(true);
            radius.setShowTickLabels(true);
            radius.setMajorTickUnit(25);
            radius.setBlockIncrement(5);
            radius.setSnapToTicks(true);

            Button farge = new Button("Velg farge");

            FargeVelger fargevelger = new FargeVelger();
            fargevelger.setColor(circle.getFill());
            
            farge.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					fargevelger.start();
				}
            });
            
            Task<Void> task = new Task<Void>() {
    
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        Platform.runLater(new Runnable(){    
                            @Override
                            public void run() {
                                //System.out.println("ball:" + circle.getRadius() + ",    slider: " + radius.getValue());
                                fartLabel.setText(String.format("Fart: %.3f", fart[0]));
                                posisjonLabel.setText(String.format("posisjon: %.1f, %.1f", circle.getCenterY(), circle.getCenterX()));
                                radiusLabel.setText("Radius: " + circle.getRadius());

                                if (!drag) {
                                    fartGraf.leggTil(Math.sqrt(Math.pow(fart[0], 2)+ Math.pow(fart[1], 2)));
                                }
                                

                                if (fargevelger.getColor() != circle.getFill()) {
                                    circle.setFill(fargevelger.getColor());
                                }

                                if (radius.getValue() != circle.getRadius()) {
                                    //System.out.println("ball:" + circle.getRadius() + ",    slider: " + radius.getValue());
                                    circle.setRadius(radius.getValue());
                                }
                            }
                        });
                        Thread.sleep(150);
                    }
                } 
            };

            Thread t1 = new Thread(task);
            t1.setDaemon(true);
            t1.start();

            Button stopp = new Button("Stopp");
            stopp.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    t1.interrupt();
                    stage.close();
                }
            });

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    t1.interrupt();
                    stage.close();
                }

            });

            vbox.getChildren().addAll(fartLabel, visGraf, posisjonLabel, radiusLabel, radius, farge, stopp);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 300, 300);
            stage.setScene(scene);
            stage.show();
        }
    }
}