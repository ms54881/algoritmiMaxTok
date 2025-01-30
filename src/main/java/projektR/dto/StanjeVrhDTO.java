package projektR.dto;

public class StanjeVrhDTO {
	
	private int visina;
    private int visakToka;
    
	public StanjeVrhDTO(int visina, int visakToka) {
		this.visina = visina;
		this.visakToka = visakToka;
	}

	public int getVisina() {
		return visina;
	}

	public void setVisina(int visina) {
		this.visina = visina;
	}

	public int getVisakToka() {
		return visakToka;
	}

	public void setVisakToka(int visakToka) {
		this.visakToka = visakToka;
	}
    
    
}
