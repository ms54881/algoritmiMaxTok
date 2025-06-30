package projektR.dto;

import java.util.List;
import java.util.Map;

public class KorakDTO {
	
	private String akcija;
	private int aktivanVrh;
	private List<StanjeBridDTO> stanjaBridova;
	private List<StanjeVrhDTO> stanjaVrhova;
	private List<List<Integer>> put; //za edmonds-karp algoritam
	private Map<Integer, Integer> razine; //za dinicov algoritam
	private String opis; //za Push Relabel
	private Integer aktivanBridPocetni;//za Push Relabel
	private Integer aktivanBridKrajnji;//za Push Relabel

	public KorakDTO(String akcija, int aktivanVrh, List<StanjeBridDTO> stanjaBridova, List<StanjeVrhDTO> stanjaVrhova) {
		this.akcija = akcija;
		this.aktivanVrh = aktivanVrh;
		this.stanjaBridova = stanjaBridova;
		this.stanjaVrhova = stanjaVrhova;
	}

	public KorakDTO() {
	}

	public String getAkcija() {
		return akcija;
	}

	public void setAkcija(String akcija) {
		this.akcija = akcija;
	}

	public int getAktivanVrh() {
		return aktivanVrh;
	}

	public void setAktivanVrh(int aktivanVrh) {
		this.aktivanVrh = aktivanVrh;
	}

	public List<StanjeBridDTO> getStanjaBridova() {
		return stanjaBridova;
	}

	public void setStanjaBridova(List<StanjeBridDTO> stanjaBridova) {
		this.stanjaBridova = stanjaBridova;
	}

	public List<StanjeVrhDTO> getStanjaVrhova() {
		return stanjaVrhova;
	}

	public void setStanjaVrhova(List<StanjeVrhDTO> stanjaVrhova) {
		this.stanjaVrhova = stanjaVrhova;
	}
	
	public List<List<Integer>> getPut() {
		return put;
	}

	public void setPut(List<List<Integer>> put) {
		this.put = put;
	}
	
	public Map<Integer, Integer> getRazine() {
		return razine;
	}

	public void setRazine(Map<Integer, Integer> razine) {
		this.razine = razine;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}
	
	public Integer getAktivanBridPocetni() {
		return aktivanBridPocetni;
	}

	public void setAktivanBridPocetni(Integer aktivanBridPocetni) {
		this.aktivanBridPocetni = aktivanBridPocetni;
	}

	public Integer getAktivanBridKrajnji() {
		return aktivanBridKrajnji;
	}

	public void setAktivanBridKrajnji(Integer aktivanBridKrajnji) {
		this.aktivanBridKrajnji = aktivanBridKrajnji;
	}
	
	
}
