package projektR.dto;

import java.util.List;

public class SimulacijaDTO {
	
	private List<KorakDTO> koraci;
    private int maksimalniTok;
    
	public SimulacijaDTO(List<KorakDTO> koraci, int maksimalniTok) {
		this.koraci = koraci;
		this.maksimalniTok = maksimalniTok;
	}

	public List<KorakDTO> getKoraci() {
		return koraci;
	}

	public void setKoraci(List<KorakDTO> koraci) {
		this.koraci = koraci;
	}

	public int getMaksimalniTok() {
		return maksimalniTok;
	}

	public void setMaksimalniTok(int maksimalniTok) {
		this.maksimalniTok = maksimalniTok;
	}
    
    
}
