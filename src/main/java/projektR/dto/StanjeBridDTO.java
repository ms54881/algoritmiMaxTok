package projektR.dto;

public class StanjeBridDTO {
	
	private int pocetniVrh;
    private int krajnjiVrh;
    private int tok;
    private int kapacitet;
    
	public StanjeBridDTO(int pocetniVrh, int krajnjiVrh, int tok, int kapacitet) {
		this.pocetniVrh = pocetniVrh;
		this.krajnjiVrh = krajnjiVrh;
		this.tok = tok;
		this.kapacitet = kapacitet;
	}

	public StanjeBridDTO() {
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

	public int getTok() {
		return tok;
	}

	public void setTok(int tok) {
		this.tok = tok;
	}

	public int getKapacitet() {
		return kapacitet;
	}

	public void setKapacitet(int kapacitet) {
		this.kapacitet = kapacitet;
	}
    
    
}
