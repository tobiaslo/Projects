
/**
 * Aapning er en underklasse til HvitRute 
 */
public class Aapning extends HvitRute{
    
    public Aapning(int rad, int kol) {
        super(rad, kol);
    }

    //Metoden sender veien til åpningen tilbake til labyrinten.
    @Override
    public void gaa(String s) {
        String send = s + " --> ( " + rad + "," + kol + " )";
        labyrint.leggTilVei(send);
    }
    
}