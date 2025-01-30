package projektR.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projektR.dto.GrafDTO;
import projektR.dto.SimulacijaDTO;
import projektR.service.PushRelabelService;

@RestController
@RequestMapping("/api/push-relabel")
public class PushRelabelController {
	
	private PushRelabelService pushRelabelService;
	
    public PushRelabelController(PushRelabelService pushRelabelService) {
        this.pushRelabelService = pushRelabelService;
    }
    
    @PostMapping("/simulacija")
    public SimulacijaDTO getSimulacija(@RequestBody(required=false) GrafDTO grafDTO) {
    	
    	if(grafDTO == null) {
    		grafDTO = pushRelabelService.primjerGraf();
    	}
    	
    	return pushRelabelService.pokreniSimulaciju(grafDTO);
    }

    @GetMapping("/primjer")
    public GrafDTO getPrimjerGraf() {
        return pushRelabelService.primjerGraf();
    }

}
