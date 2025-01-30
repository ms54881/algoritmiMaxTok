package projektR.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projektR.dto.GrafDTO;
import projektR.dto.SimulacijaDTO;
import projektR.service.EdmondKarpService;

@RestController
@RequestMapping("/api/edmondkarp")
public class EdmondKarpController {

    private EdmondKarpService edmondKarpService; 

    public EdmondKarpController(EdmondKarpService edmondKarpService) {
		this.edmondKarpService = edmondKarpService;
	}

	@PostMapping("/simulacija")
    public SimulacijaDTO pokreniSimulaciju(@RequestBody(required=false) GrafDTO grafDTO) {
        
    	if(grafDTO == null) {
    		grafDTO = edmondKarpService.primjerGraf();
    	}
    	
    	return edmondKarpService.pokreniSimulaciju(grafDTO);
    }

    @GetMapping("/primjer")
    public GrafDTO getPrimjerGraf() {
        return edmondKarpService.primjerGraf();
    }
 }
