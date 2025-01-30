package projektR.domain;

import java.util.ArrayList;
import java.util.List;

public class Graf {
	int brojVrhova;
	List<Vrh> vrhovi;
	List<Brid> bridovi = new ArrayList<>();
	
	public Graf(int brojVrhova, List<Brid> bridovi) {
		this.brojVrhova = brojVrhova;
		this.bridovi = bridovi;
	}
	
	 public Graf(int brojVrhova) {
	        this.brojVrhova = brojVrhova;
	        vrhovi = new ArrayList<>();
	        bridovi = new ArrayList<>();

	        for (int i = 0; i < brojVrhova; i++) {
	        	Vrh v = new Vrh(0, 0);
	        	vrhovi.add(v);
	        }
	    }

	    public void dodajBrid(int pocetniVrh, int krajnjiVrh, int kapacitet) {
	    	Brid b = new Brid(0, kapacitet, pocetniVrh, krajnjiVrh);
	        bridovi.add(b);
	    }

	public int getBrojVrhova() {
		return brojVrhova;
	}

	public void setBrojVrhova(int brojVrhova) {
		this.brojVrhova = brojVrhova;
	}

	public List<Brid> getBridovi() {
		return bridovi;
	}

	public void setBridovi(List<Brid> bridovi) {
		this.bridovi = bridovi;
	}
	
	

}
