import javafx.scene.paint.Color;

/**
 * Klassen Rute er superklasse for de andre rutene, inneholder ulike funksjoner
 */
abstract class Rute {

    int rad;
    int kol;
    Labyrint labyrint;
    Rute nord, syd, vest, ost;

    public Rute(int r, int k) {
        rad = r;
        kol = k;
    }

    public String hentPo() {
        return rad + "," + kol;
    }

    //Setter naboene til Ruten
    public void settNabo(Rute n, Rute s, Rute v, Rute o) {
        nord = n;
        syd = s;
        vest = v;
        ost = o;
    }

    //Lagrer labyrinten Ruten er en del av
    public void settLab(Labyrint l) {
        labyrint = l;
    }

    //Finner utveiene med hjelp av rekusjon.
    public void gaa(String s) {
        Rute[] liste = {nord, syd, vest, ost};
        String send;
        if (s == null) {
            send = "( " + rad + "," + kol + " )";
        } else {
            send = s + " --> ( " + rad + "," + kol + " )";
        }

        for (Rute rut : liste) {
            if (!send.contains(" " + rut.rad + "," + rut.kol + " ") && rut != null) {
                rut.gaa(send);
            }
        }
    }

    abstract char tilTegn();

    abstract Color hentFarge();
}