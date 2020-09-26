import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Inneholder diverse metoder for å vise, lage og finne aapninger i en labyrint
 */
public class Labyrint {

    //Finner utveiene i labyrinten og bytter dem fra klassen HvitRute til Aapning
    private static Rute[][] utvei(Rute[][] r, int rad, int kol) {
        Rute[][] rut = r;
        for (int k = 0; k < kol; k++) {
            if (rut[0][k].tilTegn() == '.') {
                rut[0][k] = new Aapning(0, k);
            }
            if (rut[rad - 1][k].tilTegn() == '.') {
                rut[rad-1][k] = new Aapning(rad - 1, k);
            }
        }

        for (int i = 0; i < rad; i++) {
            if (rut[i][0].tilTegn() == '.') {
                rut[i][0] = new Aapning(i, 0);
            }
            if (rut[i][kol-1].tilTegn() == '.') {
                rut[i][kol-1] = new Aapning(i, kol-1);
            }
        }

        return rut;
    }

    //Leser en fil og lager en labyrint med Rute obkjekter
    static Labyrint lesFraFil(File fil) throws FileNotFoundException {
        Scanner scanner = new Scanner(fil);
        String[] storrelseS = scanner.nextLine().split(" ");
        Rute[][] rut = new Rute[Integer.parseInt(storrelseS[0])][Integer.parseInt(storrelseS[1])];
        Labyrint labyrint;

        //lager en array med ruter
        for (int i = 0; i < Integer.parseInt(storrelseS[0]); i++) {
            String rad = scanner.nextLine();
            for (int k = 0; k < Integer.parseInt(storrelseS[1]); k++) {
                if (rad.charAt(k) == '.') {
                    rut[i][k] = new HvitRute(i, k);
                } else if (rad.charAt(k) == '#') {
                    rut[i][k] = new SortRute(i, k);
                } else {
                    System.out.println("hmmmm");
                }
            }
        }

        //lager en labyrint
        labyrint = new Labyrint(rut, Integer.parseInt(storrelseS[0]), Integer.parseInt(storrelseS[1]));

        rut = utvei(rut, Integer.parseInt(storrelseS[0]), Integer.parseInt(storrelseS[1]));
        //setter naboene til rutene i arrayet
        for (int i = 0; i < Integer.parseInt(storrelseS[0]); i++) {
            for (int k = 0; k < Integer.parseInt(storrelseS[1]); k++) {
                Rute nord;
                Rute syd;
                Rute ost;
                Rute vest;

                if (i == 0) {
                    nord = null;
                    syd = rut[i+1][k];
                } else if (i == Integer.parseInt(storrelseS[0]) - 1) {
                    nord = rut[i-1][k];
                    syd = null;
                } else {
                    nord = rut[i-1][k];
                    syd = rut[i+1][k];
                }
                if (k == 0) {
                    vest = null;
                    ost = rut[i][k+1];
                } else if (k == Integer.parseInt(storrelseS[1]) - 1) {
                    vest = rut[i][k-1];
                    ost = null;
                } else {
                    vest = rut[i][k-1];
                    ost = rut[i][k+1];
                }

                rut[i][k].settNabo(nord, syd, vest, ost);
                rut[i][k].settLab(labyrint);
            }
        }

        scanner.close();
        return labyrint;
    }

    private int rader;
    private int kolonner;
    private Rute[][] ruter;
    private Lenkeliste<String> veier;

    private Labyrint(Rute[][] r, int ra, int ko) {
        ruter = r;
        rader = ra;
        kolonner = ko;
        veier = new Lenkeliste<String>();
    }

    public Rute[][] hentRuter() {
        return ruter;
    }

    public int hentRad() {
        return rader;
    }

    public int hentKol() {
        return kolonner;
    }

    //Printer ut labyrinten
    @Override
    public String toString() {
        String string = "";
        for (Rute[] rad : ruter) {
            for (Rute r : rad) {
                string += r.tilTegn();
            }
            string += "\n";
        }
        return string;
    }

    //Metoden tar inn et punkt og skal finne utveier fra punktet
    public Lenkeliste<String> finnUtveiFra(int r, int k) {
        veier = new Lenkeliste<String>();
        ruter[r][k].gaa(null);
        return veier;
    }

    //legger til veier i en liste
    public void leggTilVei(String s) {
        veier.leggTil(s);
    }

    //Finner den korteste veien fra et punkt
    public String kortesteVei(int r, int k) {
        Lenkeliste<String> veierListe = finnUtveiFra(r, k);
        String kortRute = veierListe.hent(0);
        for (String vei : veierListe) {
            if (vei.split("-->").length < kortRute.split("-->").length) {
                kortRute = vei;
            }
        }
        System.out.println(kortRute);

        return kortRute;

        //return "Dette var den korteste ruten: \n" + kortRute + "\nTotalt var det " + veierListe.stoerrelse() + "\n";
    }
}