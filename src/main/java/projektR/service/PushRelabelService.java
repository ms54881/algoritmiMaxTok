package projektR.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import projektR.domain.Graf;
import projektR.domain.GrafPR;
import projektR.dto.BridDTO;
import projektR.dto.GrafDTO;
import projektR.dto.SimulacijaDTO;

@Service
public class PushRelabelService {
	
	private GrafPR grafPR;
	
	public PushRelabelService(GrafPR grafPR) {
		this.grafPR = grafPR;
	}
	
	public SimulacijaDTO pokreniSimulaciju(GrafDTO grafDTO) {
		
		Graf graf = new Graf(grafDTO.getBrojVrhova());
		int izvor = grafDTO.getIzvor();
		int ponor = grafDTO.getPonor();
		
		for(BridDTO b : grafDTO.getBridovi()) {
			graf.dodajBrid(b.getPocetniVrh(), b.getKrajnjiVrh(), b.getKapacitet());
			
		}
		
		SimulacijaDTO simulacija = grafPR.maksimalniTokSimulacija(graf, izvor, ponor);
		
		return simulacija;
	}
	
	public GrafDTO primjerGraf() {
        GrafDTO grafPrimjer = new GrafDTO();
        grafPrimjer.setBrojVrhova(6);
        grafPrimjer.setIzvor(0);
        grafPrimjer.setPonor(5);

        List<BridDTO> bridovi = new ArrayList<>();
        bridovi.add(new BridDTO(0,1,16));
        bridovi.add(new BridDTO(0,2,13));
        bridovi.add(new BridDTO(1,2,10));
        bridovi.add(new BridDTO(2,1,4));
        bridovi.add(new BridDTO(1,3,12));
        bridovi.add(new BridDTO(2,4,14));
        bridovi.add(new BridDTO(3,2,9));
        bridovi.add(new BridDTO(3,5,20));
        bridovi.add(new BridDTO(4,3,7));
        bridovi.add(new BridDTO(4,5,4));

        grafPrimjer.setBridovi(bridovi);
        return grafPrimjer;
    }
}
