import javafx.scene.paint.Color;

/**
 * Underklasse til Rute
 */
public class SortRute extends Rute {

    public SortRute(int r, int k) {
        super(r, k);
    }

    @Override
    char tilTegn() {
        return '#';
    }

    // Overskriver moetoden gaa til å gjøre ingenting
    @Override
    public void gaa(String s) {

    }

    @Override
    public Color hentFarge() {
        return Color.BLACK;
    }

    
}