
class UgyldigListeIndeks extends RuntimeException {
    public UgyldigListeIndeks(int indeks) {
        super("Ugyldig indeks:" + indeks);
    }
}