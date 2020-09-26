import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FargeVelger {

    private Paint farge = Color.BLACK;
    Stage stage;

    public FargeVelger() {
        stage = new Stage();
        setUp();
    }

    public FargeVelger(Paint p) {
        stage = new Stage();
        farge = p;
        setUp();
    }

    public void setUp() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Farger");
        stage.setAlwaysOnTop(true);

        Rectangle[] liste = new Rectangle[9];
        for (int i = 0; i<liste.length; i++) {
            Rectangle r = new Rectangle(50, 50);
            r.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    System.out.println("setter: " + r.getFill()); 
                    farge = r.getFill();
                    stage.close();
                }
            });
            liste[i] = r;
        }

        GridPane grid = new GridPane();
        liste[0].setFill(Color.BLACK);
        liste[1].setFill(Color.RED);
        liste[2].setFill(Color.GREEN);
        liste[3].setFill(Color.BLUE);
        liste[4].setFill(Color.PINK);
        liste[5].setFill(Color.BROWN);
        liste[6].setFill(Color.YELLOW);
        liste[7].setFill(Color.ORANGE);
        liste[8].setFill(Color.INDIGO);

        for (int i = 0; i<3; i++) {
            for (int k = 0; k<3; k++) {
                grid.add(liste[(i*3) + k], i, k);
            }
        }

        Scene scene = new Scene(grid);
        stage.setScene(scene);
    }

    public Paint getColor() {
        return farge;
    }

    public void setColor(Paint f) {
        farge = f;
    }

    public void start() {
        stage.show();
    }
}