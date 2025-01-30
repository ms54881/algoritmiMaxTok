package projektR.domain;

import org.springframework.stereotype.Component;
import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;

import java.util.*;

@Component
public class Dinic {
	
	private boolean bfs(GrafDinic grafDinic, int izvor, int ponor) {
	
		
	    for(int i = 0; i < grafDinic.brojVrhova; i++) {
	    	grafDinic.razine[i] = -1;
	    }
	    
	    grafDinic.razine[izvor] = 0;

	    Queue<Integer> red = new LinkedList<>();
	    red.add(izvor);

	    while (!red.isEmpty()) {
	        int u = red.poll();
	        System.out.println("BFS obrađuje čvor: " + u);

	        for (BridDinic brid : grafDinic.bridovi[u]) { 
	            if (grafDinic.razine[brid.krajnjiVrh] == -1 && brid.tok < brid.kapacitet) {
	            	grafDinic.razine[brid.krajnjiVrh] = grafDinic.razine[u] + 1;
	            	System.out.println("Postavljena razina čvora " + brid.krajnjiVrh + " na " + grafDinic.razine[brid.krajnjiVrh]);
	            	red.add(brid.krajnjiVrh);
	            }
	        }
	    }
	    System.out.println("Nivo-graf završen. Razina ponora: " + grafDinic.razine[ponor]);
	    return grafDinic.razine[ponor] != -1;
	}


    private int dfs(GrafDinic grafDinic, int u, int ponor, int tok, int[] pointer) {
        if (u == ponor) {
        	System.out.println("Pogođen ponor s protokom: " + tok);
        	return tok;
        }

        for (; pointer[u] < grafDinic.bridovi[u].size(); pointer[u]++) {
        	
            BridDinic bridDinic = grafDinic.bridovi[u].get(pointer[u]);

            if (grafDinic.razine[bridDinic.krajnjiVrh] == grafDinic.razine[u]+1 && bridDinic.tok < bridDinic.kapacitet) {
                int rezidualniKapacitet = bridDinic.kapacitet - bridDinic.tok;
                System.out.println("DFS pokušava kroz brid " + u + " -> " + bridDinic.krajnjiVrh + " s kapacitetom: " + rezidualniKapacitet);

                int pushed = dfs(grafDinic, bridDinic.krajnjiVrh, ponor, Math.min(tok, rezidualniKapacitet), pointer);

                if (pushed > 0) {
                    bridDinic.tok += pushed;

                    // Backward BridDinic
                    BridDinic povratniBridDinic = grafDinic.bridovi[bridDinic.krajnjiVrh].get(bridDinic.indeksRez);

                    povratniBridDinic.tok -= pushed;
                    System.out.println("Tok " + pushed + " dodan kroz brid " + u + " -> " + bridDinic.krajnjiVrh);
                    return pushed;
                }
            }
        }

        return 0;
    }


    private KorakDTO snimiStanje(GrafDinic grafDinic, String akcija) {
        KorakDTO korak = new KorakDTO();
        korak.setAkcija(akcija);

        List<StanjeBridDTO> bridoviDTO = new ArrayList<>();
        for (int i = 0; i < grafDinic.brojVrhova; i++) {
            for (BridDinic brid : grafDinic.bridovi[i]) {
                StanjeBridDTO stanje = new StanjeBridDTO();
                stanje.setPocetniVrh(brid.pocetniVrh);
                stanje.setKrajnjiVrh(brid.krajnjiVrh);
                stanje.setTok(brid.tok);
                stanje.setKapacitet(brid.kapacitet);
                bridoviDTO.add(stanje);
            }
        }
        korak.setStanjaBridova(bridoviDTO);

        return korak;
    }
    
    public SimulacijaDTO dinicSimulacija(GrafDinic grafDinic, int izvor, int ponor) {
    	System.out.println("Pokrenuta simulacija Dinicovog algoritma...");
        List<KorakDTO> koraci = new ArrayList<>();
        int maksimalniTok = 0;

        while (bfs(grafDinic, izvor, ponor)) {
        	System.out.println("Pronađen nivo-graf, pokrećem DFS...");

            int[] pointer = new int[grafDinic.brojVrhova];
            int protok;

            while(true) {
            	protok = dfs(grafDinic, izvor, ponor, Integer.MAX_VALUE, pointer);
            	if(protok == 0) break;
                maksimalniTok += protok;
                System.out.println("Dodana augmentacija: " + protok);
                koraci.add(snimiStanje(grafDinic, "Augmentacija protoka: " + protok));
            }
            System.out.println("Završena iteracija DFS-a, maksimalni tok sada: " + maksimalniTok);
        }
        
        System.out.println("Simulacija završena, maksimalni tok: " + maksimalniTok);
        return new SimulacijaDTO(koraci, maksimalniTok);
    }

}

