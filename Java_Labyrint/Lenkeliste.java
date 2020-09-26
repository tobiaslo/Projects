import java.util.Iterator;


class Lenkeliste<T> implements Liste<T> {
    protected int storrelse;
    protected Node<T> liste;

    protected class Node<T> {
        private Node<T> neste;
        private Node<T> forrige;
        private T data;
    
        protected Node(T t) {
            data = t;
        }
    
        protected void settNeste(Node<T> n) {
            neste = n;
        }

        protected void settForrige(Node<T> n) {
            forrige = n;
        }
    
        protected Node<T> hentNeste() {
            return neste;
        }

        protected Node<T> hentForrige() {
            return forrige;
        }

        protected T hentData() {
            return data;
        }

        protected void settData(T d) {
            data = d;
        }
    }

    public Lenkeliste() {
        storrelse = 0;
        liste = null;
    }

    protected Node<T> hentPos(int pos) {
        Node<T> n = liste;
        for (int i = 0; i < pos; i++) {
            n = n.hentNeste();
        }
        return n;
    }

    protected Node<T> hentSiste() {
        Node<T> nesteNode = liste;
        while (nesteNode.hentNeste() != null) {
            nesteNode = nesteNode.hentNeste();
        }
        return nesteNode;
    }

    @Override
    public int stoerrelse() {
        return storrelse;
    }

    @Override
    public void leggTil(int pos, T x) {
        Node<T> n = liste;

        if (pos == 0) {
            if (n == null) {
                liste = new Node<T>(x);
                storrelse++;
            }

            else {
                n = new Node<T>(x);
                n.settNeste(liste);
                liste.settForrige(n);
                liste = n;
                storrelse++;
            }
        }

        else if (pos > storrelse || pos < 0) {
            throw new UgyldigListeIndeks(pos);
        }

        else {
            Node<T> nyNode = new Node<T>(x);

            if (pos == storrelse) {
                leggTil(x);
            }

            else {
                n = hentPos(pos);
                nyNode.settNeste(n);
                nyNode.settForrige(n.hentForrige());
                n.hentForrige().settNeste(nyNode);
                n.settForrige(nyNode);
                storrelse++;
            }      
        }     
    }

    @Override
    public void leggTil(T x) {
        Node<T> nesteNode = liste;
        if (nesteNode == null) {
            liste = new Node<T>(x);
        }

        else {
            while (nesteNode.hentNeste() != null) {
                nesteNode = nesteNode.hentNeste();
            }
            Node<T> nynode = new Node<T>(x);
            nesteNode.settNeste(nynode);
            nynode.settForrige(nesteNode);
        }
        storrelse++;
    }

    @Override
    public void sett(int pos, T x) {
        if (pos >= storrelse || pos < 0) {
            throw new UgyldigListeIndeks(pos);
        }

        else {
            Node<T> n = hentPos(pos);
            n.settData(x);
        }
    }

    @Override
    public T hent(int pos) {
        if (pos >= storrelse || pos < 0) {
            throw new UgyldigListeIndeks(pos);
        }

        else {
            Node<T> n = hentPos(pos);
            return n.hentData();
        }
    }

    @Override
    public T fjern(int pos) {
        T fjernet;
        if (pos >= storrelse || pos < 0) {
            throw new UgyldigListeIndeks(pos);  
        }

        else if (pos == 0) {
            fjernet = fjern();
        }

        else if (pos == storrelse - 1) {
            Node<T> n = liste;
            while (n.hentNeste() != null) {
                n = n.hentNeste();
            }

            fjernet = n.hentData();
            n.hentForrige().settNeste(null);
            storrelse--;
        }

        else {
            Node<T> n = hentPos(pos);

            fjernet = n.hentData();
            n.hentForrige().settNeste(n.hentNeste());
            n.hentNeste().settForrige(n.hentForrige());
            storrelse--;
            
        }
        return fjernet;
    }

    @Override
    public T fjern() {
        if (liste == null) {
            throw new UgyldigListeIndeks(0);
        }
        else {
            T fjernet = liste.hentData();
            liste = liste.hentNeste();
            storrelse--;
            return fjernet;
        }
    }

    class Lenkelisteiterator implements Iterator<T> {
        private int i = 0;
        @Override
        public boolean hasNext() {
            return (i < storrelse);
        }

        @Override
        public T next() {
            
            return (hent(i++));
        }

    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> iter = new Lenkelisteiterator();
        return iter;
    }
}