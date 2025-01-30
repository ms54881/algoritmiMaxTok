package projektR.domain;

public class BridDinic {
    int kapacitet;    
    int tok;          
    int pocetniVrh;   
    int krajnjiVrh;   
    int indeksRez;          

    public BridDinic(int kapacitet, int tok, int pocetniVrh, int krajnjiVrh, int indeksRez) {
        this.kapacitet = kapacitet;
        this.tok = tok;
        this.pocetniVrh = pocetniVrh;
        this.krajnjiVrh = krajnjiVrh;
        this.indeksRez = indeksRez;
    }

    public int getKapacitet() {
        return kapacitet;
    }

    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }

    public int getTok() {
        return tok;
    }

    public void setTok(int tok) {
        this.tok = tok;
    }

    public int getPocetniVrh() {
        return pocetniVrh;
    }

    public void setPocetniVrh(int pocetniVrh) {
        this.pocetniVrh = pocetniVrh;
    }

    public int getKrajnjiVrh() {
        return krajnjiVrh;
    }

    public void setKrajnjiVrh(int krajnjiVrh) {
        this.krajnjiVrh = krajnjiVrh;
    }

    public int getindeksRez() {
        return indeksRez;
    }

    public void setindeksRez(int indeksRez) {
        this.indeksRez = indeksRez;
    }
}

