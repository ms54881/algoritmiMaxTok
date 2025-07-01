package projektR.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;
import projektR.dto.StanjeVrhDTO;

@Component
public class GrafPR { 
	
    public GrafPR() {
	}

	public Graf pripremiPredtok(Graf graf, int izvor) {
		graf.vrhovi.get(izvor).visina = graf.vrhovi.size(); //prvo postavlja visinu izvora na broj vrhova

        for (int i = 0; i < graf.bridovi.size(); i++) {
            if (graf.bridovi.get(i).pocetniVrh == izvor) { 
            	graf.bridovi.get(i).tok = graf.bridovi.get(i).kapacitet; //za bridove čiji je početni vrh izvor postavlja tok na kapacitet
            	graf.vrhovi.get(graf.bridovi.get(i).krajnjiVrh).visakToka += graf.bridovi.get(i).tok; //doda taj tok u varijablu višakToka za krajnji vrh
            	graf.bridovi.add(new Brid(-graf.bridovi.get(i).tok, 0, graf.bridovi.get(i).krajnjiVrh, izvor)); //postavi obrnuti smjer(rezidualni graf)
            }
        }
        return graf;
    }

    public int vrhSViskomToka(Graf graf) {
        for (int i = 1; i < graf.vrhovi.size() - 1; i++) //preskače izvor i ponor
            if (graf.vrhovi.get(i).visakToka > 0)
                return i; //vraća prvi vrh koji ima visakToka, tj. aktivan vrh
        return -1;
    }

    public void suprotniTok(Graf graf, int i, int tok) {
        int pocetniVrh = graf.bridovi.get(i).krajnjiVrh, krajnjiVrh = graf.bridovi.get(i).pocetniVrh;

        for (int j = 0; j < graf.bridovi.size(); j++) {
            if (graf.bridovi.get(j).krajnjiVrh == krajnjiVrh && graf.bridovi.get(j).pocetniVrh == pocetniVrh) {
            	graf.bridovi.get(j).tok -= tok;
                return;
            }
        }

        graf.bridovi.add(new Brid(0, -tok, pocetniVrh, krajnjiVrh));
    }

    public int guraj(Graf graf, int vrh) {
        for (int i = 0; i < graf.bridovi.size(); i++) {
            if (graf.bridovi.get(i).pocetniVrh == vrh) {
                if (graf.bridovi.get(i).tok == graf.bridovi.get(i).kapacitet)
                    continue; //Brid je zasićen

                if (graf.vrhovi.get(vrh).visina > graf.vrhovi.get(graf.bridovi.get(i).krajnjiVrh).visina) {
                    int tok = Math.min(graf.bridovi.get(i).kapacitet - graf.bridovi.get(i).tok, graf.vrhovi.get(vrh).visakToka);
                    graf.vrhovi.get(vrh).visakToka -= tok;
                    graf.vrhovi.get(graf.bridovi.get(i).krajnjiVrh).visakToka += tok;
                    graf.bridovi.get(i).tok += tok;
                    suprotniTok(graf, i, tok);
                    
                    graf.setAktivanPocetni(graf.bridovi.get(i).pocetniVrh);
                    graf.setAktivanKrajnji(graf.bridovi.get(i).krajnjiVrh);
                    return tok;
                }
            }
        }
        return 0; //guranje nije moguće
    }

    public void promijeniVisinu(Graf graf, int vrh) {
        int najmanjaVisina = Integer.MAX_VALUE;

        for (int i = 0; i < graf.bridovi.size(); i++) {
            if (graf.bridovi.get(i).pocetniVrh == vrh) {
                if (graf.bridovi.get(i).tok == graf.bridovi.get(i).kapacitet)
                    continue;

                najmanjaVisina = Math.min(najmanjaVisina, graf.vrhovi.get(graf.bridovi.get(i).krajnjiVrh).visina);
                graf.vrhovi.get(vrh).visina = najmanjaVisina + 1;
            }
        }
    }
    
    private KorakDTO snimiStanje(Graf graf, String akcija, int vrh, Integer povecanjeToka) {
    	
    	List<StanjeBridDTO> bridoviDTO = new ArrayList<>();
    	List<StanjeVrhDTO> vrhoviDTO = new ArrayList<>();
    	
    	for(Brid b : graf.bridovi) {
    		StanjeBridDTO sb = new StanjeBridDTO(b.pocetniVrh, b.krajnjiVrh, b.tok, b.kapacitet);
    		bridoviDTO.add(sb);
    	}
    	
    	for(Vrh v : graf.vrhovi) {
    		StanjeVrhDTO sv = new StanjeVrhDTO(v.visina, v.visakToka);
    		vrhoviDTO.add(sv);
    	}
    	
    	KorakDTO korak = new KorakDTO(akcija, vrh, bridoviDTO, vrhoviDTO);

    	
    	if ("guraj".equals(akcija)) {
            korak.setOpis("Operacija PUSH – povećanje toka za " + povecanjeToka);
            korak.setAktivanBridPocetni(graf.aktivanPocetni);
            korak.setAktivanBridKrajnji(graf.aktivanKrajnji);
        } else if ("promijeniVisinu".equals(akcija)) {
            korak.setOpis("Operacija RELABEL – promjena visine vrha " + vrh);
        }
    	
    	return korak;
    }

    public SimulacijaDTO maksimalniTokSimulacija(Graf graf, int izvor, int ponor) {
        pripremiPredtok(graf, izvor);
        
        List<KorakDTO> koraci = new ArrayList<>();

        while (vrhSViskomToka(graf) != -1) {
            int vrh = vrhSViskomToka(graf);
            int guranje = guraj(graf, vrh);
            
            if (guranje > 0) {
                koraci.add(snimiStanje(graf, "guraj", vrh, guranje));
            }
            
            if (guranje == 0) {
            	promijeniVisinu(graf, vrh);
            	koraci.add(snimiStanje(graf, "promijeniVisinu", vrh, null));
        }
            System.out.println("prošao");
        }
        
        // Postavi opis za prvi korak
        if (!koraci.isEmpty()) {
            koraci.get(0).setOpis("Inicijalizacija predtoka");
        }
        
        int maksimalanTok = graf.vrhovi.get(ponor).visakToka;
        SimulacijaDTO simulacija = new SimulacijaDTO(koraci, maksimalanTok);
        System.out.println("Broj koraka: " + koraci.size());
        
        return simulacija;
	
    }
}

