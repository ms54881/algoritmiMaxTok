package projektR.domain;

import org.springframework.stereotype.Component;
import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;

import java.util.*;

@Component
public class Dinic {
	
	private String formirajOpisRazinskogGrafa(int[] razine) {
		int maxRazina = Arrays.stream(razine)
                .filter(r -> r >= 0)
                .max()
                .orElse(0);

return "Razinski graf izgrađen BFS algoritmom u " + (maxRazina + 1) + " razina.";
	}
	
	private String formirajOpisAugmentacije(List<List<Integer>> put, int protok) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < put.size(); i++) {
	        List<Integer> par = put.get(i);
	        sb.append(par.get(0)).append(" → ").append(par.get(1));
	        if (i < put.size() - 1) sb.append(" → ");
	    }
	    sb.append(" (povećanje toka za ").append(protok).append(")");
	    return "Povećavajući put pronađen DFS algoritmom: " + sb.toString();
	}
	
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


    private int dfs(GrafDinic grafDinic, int u, int ponor, int tok, int[] pointer, List<List<Integer>> aktivniPut) {
        if (u == ponor) {
        	System.out.println("Pogođen ponor s protokom: " + tok);
        	return tok;
        }

        for (; pointer[u] < grafDinic.bridovi[u].size(); pointer[u]++) {
        	
            BridDinic bridDinic = grafDinic.bridovi[u].get(pointer[u]);

            if (grafDinic.razine[bridDinic.krajnjiVrh] == grafDinic.razine[u]+1 && bridDinic.tok < bridDinic.kapacitet) {
                int rezidualniKapacitet = bridDinic.kapacitet - bridDinic.tok;
                System.out.println("DFS pokušava kroz brid " + u + " -> " + bridDinic.krajnjiVrh + " s kapacitetom: " + rezidualniKapacitet);

                aktivniPut.add(List.of(u, bridDinic.krajnjiVrh));
                
                int pushed = dfs(grafDinic, bridDinic.krajnjiVrh, ponor, Math.min(tok, rezidualniKapacitet), pointer, aktivniPut);

                if (pushed > 0) {
                    bridDinic.tok += pushed;

                    // Backward BridDinic
                    BridDinic povratniBridDinic = grafDinic.bridovi[bridDinic.krajnjiVrh].get(bridDinic.indeksRez);

                    povratniBridDinic.tok -= pushed;
                    System.out.println("Tok " + pushed + " dodan kroz brid " + u + " -> " + bridDinic.krajnjiVrh);
                    return pushed;
                }
             // Ako nije uspjelo, ukloni zadnji dodani brid
                aktivniPut.remove(aktivniPut.size() - 1);
            }
        }

        return 0;
    }


    private KorakDTO snimiStanje(GrafDinic grafDinic, String akcija, List<List<Integer>> put) {
        KorakDTO korak = new KorakDTO();
        korak.setAkcija(akcija);
        korak.setPut(put);

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
        Map<Integer, Integer> razineMapa = new HashMap<>();
        for (int i = 0; i < grafDinic.brojVrhova; i++) {
            razineMapa.put(i, grafDinic.razine[i]);
        }
        korak.setRazine(razineMapa);

        return korak;
    }
    
    public SimulacijaDTO dinicSimulacija(GrafDinic grafDinic, int izvor, int ponor) {
    	System.out.println("Pokrenuta simulacija Dinicovog algoritma...");
        List<KorakDTO> koraci = new ArrayList<>();
        int maksimalniTok = 0;

        while (bfs(grafDinic, izvor, ponor)) {
        	System.out.println("Pronađen nivo-graf, pokrećem DFS...");
        	String opisRazinskog = formirajOpisRazinskogGrafa(grafDinic.razine);
            koraci.add(snimiStanje(grafDinic, opisRazinskog, null)); // put je null jer nije augmentacija

            int[] pointer = new int[grafDinic.brojVrhova];
            int protok;

            while(true) {
            	List<List<Integer>> put = new ArrayList<>();
            	protok = dfs(grafDinic, izvor, ponor, Integer.MAX_VALUE, pointer, put);
            	if(protok == 0) break;
                maksimalniTok += protok;
                System.out.println("Dodana augmentacija: " + protok);
                

                // 3) Dodaj korak s augmentacijom
                String opisKoraka = formirajOpisAugmentacije(put, protok);
                koraci.add(snimiStanje(grafDinic, opisKoraka, put));
              
           
            }
            System.out.println("Završena iteracija DFS-a, maksimalni tok sada: " + maksimalniTok);
        }
        
        System.out.println("Simulacija završena, maksimalni tok: " + maksimalniTok);
        return new SimulacijaDTO(koraci, maksimalniTok);
    }

}

