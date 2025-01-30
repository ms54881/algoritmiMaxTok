package projektR.rest;

import org.springframework.web.bind.annotation.*;
import projektR.dto.GrafDTO;
import projektR.dto.SimulacijaDTO;
import projektR.service.DinicService;

@RestController
@RequestMapping("/api/dinic")
public class DinicController {
	
    private DinicService dinicService;

    public DinicController(DinicService dinicService) {
        this.dinicService = dinicService;
    }

    @PostMapping("/simulacija")
    public SimulacijaDTO pokreniSimulaciju(@RequestBody(required=false) GrafDTO grafDTO) {
        System.out.println("Zaprimljen zahtjev za Dinicovu simulaciju!");
        System.out.println("Broj vrhova: " + grafDTO.getBrojVrhova());
        System.out.println("Bridovi: " + grafDTO.getBridovi().size());
        
    	return dinicService.pokreniSimulaciju(grafDTO);
    }

    @GetMapping("/primjer")
    public GrafDTO getPrimjerGraf() {
        return dinicService.primjerGraf();
    }
}

