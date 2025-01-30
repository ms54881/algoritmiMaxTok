package projektR.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;

import projektR.dto.KorakDTO;
import projektR.dto.SimulacijaDTO;
import projektR.dto.StanjeBridDTO;

@Component
public class EdmondKarp {
	
	public EdmondKarp() {
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
	    private KorakDTO snimiStanje(Graf graf, String akcija) {
	        KorakDTO korak = new KorakDTO();
	        korak.setAkcija(akcija);

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

	        // Ako želite i stanja vrhova, kreirajte listu VrhStanjeDTO
	        // korak.setVrhovi(...) // ako je potrebno

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
			koraci.add(snimiStanje(graf, "augmentacija puta i BFS"));
			
		}
		
		return new SimulacijaDTO(koraci, maksimalniTok);
	}

}
