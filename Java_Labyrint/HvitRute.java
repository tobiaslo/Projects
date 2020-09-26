import javafx.scene.paint.Color;

/**
 * Underklasse til rute.
 */
public class HvitRute extends Rute {

    public HvitRute(int r, int k) {
        super(r, k);
    }

    @Override
    char tilTegn() {
        return '.';
    }

    @Override
    public Color hentFarge() {
        return Color.WHITE;
    }
    
}