package projektR.domain;

//klasa s osnovnim elementima brida: tok, kapacitet, te početni i krajnjiVrh
public class Brid {
	
	int tok;
	int kapacitet;
	int pocetniVrh;
	int krajnjiVrh;

    public Brid(int tok, int kapacitet, int pocetniVrh, int krajnjiVrh) {
        this.tok = tok;
        this.kapacitet = kapacitet;
        this.pocetniVrh = pocetniVrh;
        this.krajnjiVrh = krajnjiVrh;
    }
	
}