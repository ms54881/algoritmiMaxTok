package projektR.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;

import ch.qos.logback.core.joran.sanity.Pair;
import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;

@Component
public class EdmondKarp {
	
	public EdmondKarp() {
	}
	
	private String formirajPut(int[] roditelj, int izvor, int ponor, int protokNaPutu) {
	    List<Integer> put = new ArrayList<>();
	    int trenutni = ponor;
	    while (trenutni != -1) {
	        put.add(0, trenutni);
	        trenutni = roditelj[trenutni];
	    }

	    // Formatiraj u string: "0 → 1 → 3 → 5 (povećanje za 5)"
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < put.size(); i++) {
	        sb.append(put.get(i));
	        if (i < put.size() - 1) {
	            sb.append(" → ");
	        }
	    }
	    sb.append(" (povećanje toka za ").append(protokNaPutu).append(")");
	    return "Povećavajući put pronađen BFS algoritmom: " + sb.toString();
	}
	
	private List<List<Integer>> generirajPutParove(int[] roditelj, int izvor, int ponor) {
	    List<List<Integer>> bridoviNaPutu = new ArrayList<>();
	    int v = ponor;
	    while (v != izvor) {
	        int u = roditelj[v];
	        bridoviNaPutu.add(0, List.of(u, v));  // dodaj na početak jer idemo od kraja
	        v = u;
	    }
	    return bridoviNaPutu;
	}

	private int[] bfsRezidualni(Graf graf, int izvor, int ponor) {
		
	    if (graf.bridovi == null || graf.bridovi.isEmpty()) {
	        throw new IllegalStateException("Lista bridova je prazna ili nije inicijalizirana!");
	    }
        // Uzmimo da Graf ima graf.brojVrhova
        int n = graf.brojVrhova;
        boolean[] posjecen = new boolean[n];
        int[] roditelj = new int[n];
        Arrays.fill(roditelj, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(izvor);
        posjecen[izvor] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            // Za svaki brid u rezidualnom smislu
            for (int i = 0; i < graf.bridovi.size(); i++) {
                Brid b = graf.bridovi.get(i);
                // Ako je b.pocetniVrh == u i b.tok < b.kapacitet => ima rezidualnog kapaciteta
                if (b.pocetniVrh == u && b.tok < b.kapacitet) {
                    int v = b.krajnjiVrh;
                    if (!posjecen[v]) {
                        posjecen[v] = true;
                        roditelj[v] = u;
                        queue.add(v);

                        if (v == ponor) {
                            // Odmah prekid ako došli do ponora
                            return roditelj;
                        }
                    }
                }
            }
        }

        // Nije doseglo ponor
        return null;
    }
	
	 private int augmentacija(Graf graf, int[] roditelj, int izvor, int ponor) {
	        int protokNaPutu = Integer.MAX_VALUE;
	        int v = ponor;
	        while (v != izvor) {
	            int u = roditelj[v];
	            Brid brid = pronadjiBrid(graf, u, v); // pronađi brid (u->v) i vidjeti rezidual = kapacitet - tok
	            int rezidualni = brid.kapacitet - brid.tok;
	            protokNaPutu = Math.min(protokNaPutu, rezidualni);

	            v = u;
	        }

	        // 2) sada ažuriramo bridove
	        v = ponor;
	        while (v != izvor) {
	            int u = roditelj[v];

	            Brid forward = pronadjiBrid(graf, u, v);
	            forward.tok += protokNaPutu;

	            // backward brid
	            Brid backward = pronadjiBrid(graf, v, u);
	            if (backward == null) {
	                // ako ne postoji, kreiraj ga s kapacitet = 0, tok negativan?
	                // ili prema push-relabel stilu, kap=0, tok=0 i sl.
	                backward = new Brid(0, 0, v, u);
	                graf.bridovi.add(backward);
	            }
	            backward.tok -= protokNaPutu;

	            v = u;
	        }

	        return protokNaPutu;
	    }

	    private Brid pronadjiBrid(Graf graf, int u, int v) {
	        for (Brid b : graf.bridovi) {
	            if (b.pocetniVrh == u && b.krajnjiVrh == v) {
	                return b;
	            }
	        }
	        return null;
	    }

	    /**
	     * Snima trenutačno stanje bridova i vrhova (ako želite)
	     */
	    private KorakDTO snimiStanje(Graf graf, String akcija, List<List<Integer>> put) {
	        KorakDTO korak = new KorakDTO();
	        korak.setAkcija(akcija);
	        korak.setPut(put);
	        
	        // Snimimo stanja bridova
	        List<StanjeBridDTO> bridoviDTO = new ArrayList<>();
	        for (Brid b : graf.bridovi) {
	            StanjeBridDTO bs = new StanjeBridDTO();
	            bs.setPocetniVrh(b.pocetniVrh);
	            bs.setKrajnjiVrh(b.krajnjiVrh);
	            bs.setTok(b.tok);
	            bs.setKapacitet(b.kapacitet);
	            bridoviDTO.add(bs);
	        }
	        korak.setStanjaBridova(bridoviDTO);


	        return korak;
	    }
	public SimulacijaDTO edmondKarpSimulacija(Graf graf, int izvor, int ponor) {
		List<KorakDTO> koraci = new ArrayList<>();
		
		int maksimalniTok = 0;
		
		while(true) {
			int[] roditelj = bfsRezidualni(graf, izvor, ponor);
			if(roditelj == null) {
				break; //nema augmentirajućeg puta
			}
			
			int protokNaPutu = augmentacija(graf, roditelj, izvor, ponor); //minimalni rezidualni kapacitet na putu
			maksimalniTok += protokNaPutu;
			String opisKoraka = formirajPut(roditelj, izvor, ponor, protokNaPutu);
			List<List<Integer>> put = generirajPutParove(roditelj, izvor, ponor);
			koraci.add(snimiStanje(graf, opisKoraka, put));
			
		}
		
		return new SimulacijaDTO(koraci, maksimalniTok);
	}

}
