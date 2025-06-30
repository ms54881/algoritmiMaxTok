package projektR.domain;

import java.util.ArrayList;
import java.util.List;

//klasa s osnovnim elementima broj vrhova i listom vrhova i bridova
public class Graf {
	int brojVrhova;
	List<Vrh> vrhovi;
	List<Brid> bridovi = new ArrayList<>();
	public Integer aktivanPocetni = null;
	public Integer aktivanKrajnji = null;
	
	public Graf(int brojVrhova, List<Brid> bridovi) {
		this.brojVrhova = brojVrhova;
		this.bridovi = bridovi;
	}
	
	 public Graf(int brojVrhova) {
	        this.brojVrhova = brojVrhova;
	        vrhovi = new ArrayList<>();
	        bridovi = new ArrayList<>();

	        for (int i = 0; i < brojVrhova; i++) {
	        	Vrh v = new Vrh(0, 0); //vrhovi se prilikom poziva konstruktora postavljaju na 0,0 za push relabel algoritam
	        	vrhovi.add(v);
	        }
	 }

	public void dodajBrid(int pocetniVrh, int krajnjiVrh, int kapacitet) {
	    	Brid b = new Brid(0, kapacitet, pocetniVrh, krajnjiVrh);
	        bridovi.add(b); //bridovi se ručno unose metodom dodaj brid
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

	public Integer getAktivanPocetni() {
		return aktivanPocetni;
	}

	public void setAktivanPocetni(Integer aktivanPocetni) {
		this.aktivanPocetni = aktivanPocetni;
	}

	public Integer getAktivanKrajnji() {
		return aktivanKrajnji;
	}

	public void setAktivanKrajnji(Integer aktivanKrajnji) {
		this.aktivanKrajnji = aktivanKrajnji;
	}
	
	
}
