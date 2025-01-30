package projektR.dto;

import java.util.ArrayList;
import java.util.List;

public class GrafDTO {
    private int brojVrhova;
    private List<BridDTO> bridovi = new ArrayList<>();
    private int izvor;
    private int ponor;
    
	public int getBrojVrhova() {
		return brojVrhova;
	}
	
	public void setBrojVrhova(int brojVrhova) {
		this.brojVrhova = brojVrhova;
	}
	
	public List<BridDTO> getBridovi() {
		return bridovi;
	}
	
	public void setBridovi(List<BridDTO> bridovi) {
		this.bridovi = bridovi;
	}
	
	public int getIzvor() {
		return izvor;
	}
	
	public void setIzvor(int izvor) {
		this.izvor = izvor;
	}
	
	public int getPonor() {
		return ponor;
	}
	
	public void setPonor(int ponor) {
		this.ponor = ponor;
	}
	 
}
