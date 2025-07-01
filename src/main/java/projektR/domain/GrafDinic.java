package projektR.domain;

import java.util.ArrayList;
import java.util.List;

//klasa Graf za Dinicov algoritam
public class GrafDinic {
	
	int brojVrhova;
    List<BridDinic>[] bridovi;
    int[] razine;

    public GrafDinic(int brojVrhova) {
        this.brojVrhova = brojVrhova;
        bridovi = new ArrayList[brojVrhova];
        for (int i = 0; i < brojVrhova; i++) {
            bridovi[i] = new ArrayList<BridDinic>();
        }
        razine = new int[brojVrhova];
    }

    public void dodajBrid(int pocetniVrh, int krajnjiVrh, int kapacitet) {
        BridDinic brid = new BridDinic(kapacitet, 0, pocetniVrh, krajnjiVrh, bridovi[krajnjiVrh].size());
        bridovi[pocetniVrh].add(brid);

        BridDinic suprotniBrid = new BridDinic(0, 0, krajnjiVrh, pocetniVrh, bridovi[pocetniVrh].size() - 1);
        bridovi[krajnjiVrh].add(suprotniBrid);
    }
}
