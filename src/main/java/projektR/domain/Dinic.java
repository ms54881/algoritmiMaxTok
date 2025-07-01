package projektR.domain;

import org.springframework.stereotype.Component;
import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;

import java.util.*;

@Component
public class Dinic {
	
	private String formirajOpisRazinskogGrafa(int[] razine) {//metoda za opis razinskog grafa na simulaciji
		int maxRazina = Arrays.stream(razine)
                .filter(r -> r >= 0)
                .max()
                .orElse(0);
		String raz = " razine.";
		if(maxRazina+1 >= 5) {
			raz = " razina.";
		}
		

return "Razinski graf izgrađen BFS algoritmom u " + (maxRazina + 1) + raz;
	}
	
	private String formirajOpisPuta(List<List<Integer>> put, int protok) {//opis puta za simulaciju
	    StringBuilder sb = new StringBuilder();
	    sb.append(put.get(0).get(0));

	    for (List<Integer> par : put) {// dodaj sve krajnje čvorove bridova
	        int to = par.get(1);
	        sb.append(" → ").append(to);
	    }

	    sb.append(" (povećanje toka za ").append(protok).append(")");
	    return "Povećavajući put pronađen DFS algoritmom: " + sb.toString();
	}
	
	private boolean bfs(GrafDinic grafDinic, int izvor, int ponor) {//gradnja razinskog grafa uz pomoć bfs
		
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
	    System.out.println("Razinski završen. Razina ponora: " + grafDinic.razine[ponor]);
	    return grafDinic.razine[ponor] != -1;
	}


    private int dfs(GrafDinic grafDinic, int u, int ponor, int tok, int[] pointer, List<List<Integer>> aktivniPut) {//povecavajuci put preko dfs
        if (u == ponor) {
        	System.out.println("Pogođen ponor s protokom: " + tok);
        	return tok;
        }

        for (; pointer[u] < grafDinic.bridovi[u].size(); pointer[u]++) {//nastavlja dalje kroz bridove
        	
            BridDinic bridDinic = grafDinic.bridovi[u].get(pointer[u]);

            if (grafDinic.razine[bridDinic.krajnjiVrh] == grafDinic.razine[u]+1 && bridDinic.tok < bridDinic.kapacitet) {//je li taj vrh na sljedecoj razini i moze li poslati tok
                int rezidualniKapacitet = bridDinic.kapacitet - bridDinic.tok;
                System.out.println("DFS pokušava kroz brid " + u + " -> " + bridDinic.krajnjiVrh + " s kapacitetom: " + rezidualniKapacitet);

                aktivniPut.add(List.of(u, bridDinic.krajnjiVrh));//pamti put
                
                int pushed = dfs(grafDinic, bridDinic.krajnjiVrh, ponor, Math.min(tok, rezidualniKapacitet), pointer, aktivniPut);//rekurzivno trazi dalje

                if (pushed > 0) {
                    bridDinic.tok += pushed;//povecaj brid ako je uspjesno

                    BridDinic povratniBridDinic = grafDinic.bridovi[bridDinic.krajnjiVrh].get(bridDinic.indeksRez);//povratni brid

                    povratniBridDinic.tok -= pushed;
                    System.out.println("Tok " + pushed + " dodan kroz brid " + u + " -> " + bridDinic.krajnjiVrh);
                    return pushed;
                }
          
                aktivniPut.remove(aktivniPut.size() - 1); // ako nije uspjelo, ukloni zadnji dodani brid
            }
        }

        return 0;
    }


    private KorakDTO snimiStanje(GrafDinic grafDinic, String akcija, List<List<Integer>> put) {//stanje svakog koraka
        KorakDTO korak = new KorakDTO();
        korak.setAkcija(akcija);
        korak.setPut(put);

        List<StanjeBridDTO> bridoviDTO = new ArrayList<>();//stanej bridova
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
        Map<Integer, Integer> razineMapa = new HashMap<>();//razine vrhova
        for (int i = 0; i < grafDinic.brojVrhova; i++) {
            razineMapa.put(i, grafDinic.razine[i]);
        }
        korak.setRazine(razineMapa);

        return korak;
    }
    
    public SimulacijaDTO dinicSimulacija(GrafDinic grafDinic, int izvor, int ponor) {//pokretanje simulacije
    	System.out.println("Pokrenuta simulacija Dinicovog algoritma...");
        List<KorakDTO> koraci = new ArrayList<>();
        int maksimalniTok = 0;

        while (bfs(grafDinic, izvor, ponor)) {
        	System.out.println("Pronađen razinski graf, kreće DFS");
        	String opisRazinskog = formirajOpisRazinskogGrafa(grafDinic.razine);
            koraci.add(snimiStanje(grafDinic, opisRazinskog, null)); // put je null jer je razinski graf

            int[] pointer = new int[grafDinic.brojVrhova];//pokazivac na kojem smo bridu u dfs
            int protok;

            while(true) {
            	List<List<Integer>> put = new ArrayList<>();
            	protok = dfs(grafDinic, izvor, ponor, Integer.MAX_VALUE, pointer, put);//pokusaj slanja toka 
            	if(protok == 0) break;//nema vise puteva - prekida
                maksimalniTok += protok;
                System.out.println("Dodana augmentacija: " + protok);
                

                String opisKoraka = formirajOpisPuta(put, protok);//opis puta za simulaciju
                koraci.add(snimiStanje(grafDinic, opisKoraka, put));
              
           
            }
            System.out.println("Završena iteracija DFS-a, maksimalni tok sada: " + maksimalniTok);
        }
        
        System.out.println("Simulacija završena, maksimalni tok: " + maksimalniTok);
        return new SimulacijaDTO(koraci, maksimalniTok);
    }

}

