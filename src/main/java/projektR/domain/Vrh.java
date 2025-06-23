package projektR.domain;

//klasa s osnovnim elementima vrha potrebna za Push-Relabel algoritam
//koji koristi oznake visine i viška toka za izvođenje
public class Vrh {
	
	int visina, visakToka;

    public Vrh(int visina, int visakToka) {
        this.visina = visina;
        this.visakToka = visakToka;
    }
}
