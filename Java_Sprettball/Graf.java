import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Graf {
    
    NumberAxis xAxis;
    NumberAxis yAxis;
    LineChart<Number, Number> lineChart;
    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
    int number;
    String navn;
    int antall;
    Scene scene;

    double max = 5.0;
    double min = -5.0;

    public Graf(String s, int a) {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        number = 1;
        navn = s;
        antall = a;
        setUp();
        scene = new Scene(lineChart);
    }

    private void setUp() {
        lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle(navn);
        lineChart.getData().add(series);

    }

    public void leggTil(double data) {
        yAxis.setAutoRanging(true);

        if (data > max) {
            yAxis.setUpperBound(data + 5);
            max = data;
        }

        if (data < min) {
            yAxis.setLowerBound(data - 5);
            min = data;
        }

        if (number > antall) {
            series.getData().remove(0);
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(number - (antall));
            xAxis.setUpperBound(number + 4);
            
        }
        
        series.getData().add(new XYChart.Data<Number, Number>(number, data));
        number++;
    }

    public LineChart<Number, Number> getGraf() {
        return lineChart;
    }

    public Scene getScene() {
        return scene;
    }
}