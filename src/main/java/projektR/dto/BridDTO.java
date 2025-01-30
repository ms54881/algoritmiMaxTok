package projektR.dto;

public class BridDTO {
    private int pocetniVrh;
    private int krajnjiVrh;
    private int kapacitet;
    
	public BridDTO(int pocetniVrh, int krajnjiVrh, int kapacitet) {
		this.pocetniVrh = pocetniVrh;
		this.krajnjiVrh = krajnjiVrh;
		this.kapacitet = kapacitet;
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
	
	public int getKapacitet() {
		return kapacitet;
	}
	
	public void setKapacitet(int kapacitet) {
		this.kapacitet = kapacitet;
	}
    
    
}
