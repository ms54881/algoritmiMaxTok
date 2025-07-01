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
	
	private String formirajPut(int[] roditelj, int izvor, int ponor, int protokNaPutu) {//put za simulaciju
	    List<Integer> put = new ArrayList<>();
	    int trenutni = ponor;
	    while (trenutni != -1) {
	        put.add(0, trenutni);
	        trenutni = roditelj[trenutni];
	    }

	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < put.size(); i++) {
	        sb.append(put.get(i));
	        if (i < put.size() - 1) {
	            sb.append(" → ");
	        }
	    }
	    sb.append(" (povećanje toka za ").append(protokNaPutu).append(")");//opis koraka za simulaciju
	    return "Povećavajući put pronađen BFS algoritmom: " + sb.toString();
	}
	
	private List<List<Integer>> generirajPutParove(int[] roditelj, int izvor, int ponor) {//generira listu u obliku parova vrhova za povećavajući put
	    List<List<Integer>> bridoviNaPutu = new ArrayList<>();
	    int v = ponor;
	    while (v != izvor) {
	        int u = roditelj[v];
	        bridoviNaPutu.add(0, List.of(u, v));  // dodaj na početak jer idemo od kraja
	        v = u;
	    }
	    return bridoviNaPutu;
	}

	private int[] bfs(Graf graf, int izvor, int ponor) {//bfs trazi povecavajuci put
		
	    if (graf.bridovi == null || graf.bridovi.isEmpty()) {
	        throw new IllegalStateException("Lista bridova je prazna ili nije inicijalizirana!");
	    }
        
        int n = graf.brojVrhova;
        boolean[] posjecen = new boolean[n];
        int[] roditelj = new int[n];
        Arrays.fill(roditelj, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.add(izvor);
        posjecen[izvor] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            
            for (int i = 0; i < graf.bridovi.size(); i++) {
                Brid b = graf.bridovi.get(i);
                
                if (b.pocetniVrh == u && b.tok < b.kapacitet) {//ako postoji rezidualni kapacitet i vrh jos nije posjecen
                    int v = b.krajnjiVrh;
                    if (!posjecen[v]) {
                        posjecen[v] = true;
                        roditelj[v] = u;
                        queue.add(v);

                        if (v == ponor) {//ako dodemo do ponora pronaden je povecavajuci put
                            return roditelj;
                        }
                    }
                }
            }
        }

     
        return null;
    }
	
	 private int slanjeToka(Graf graf, int[] roditelj, int izvor, int ponor) {//trazi min rez kapacitet
	        int protokNaPutu = Integer.MAX_VALUE;
	        int v = ponor;
	        while (v != izvor) {
	            int u = roditelj[v];
	            Brid brid = pronadjiBrid(graf, u, v); // pronađi brid (u->v) 
	            int rezidualni = brid.kapacitet - brid.tok;
	            protokNaPutu = Math.min(protokNaPutu, rezidualni);

	            v = u;
	        }

	        
	        v = ponor;
	        while (v != izvor) {//azuriranje bridova i povratnih bridova
	            int u = roditelj[v];

	            Brid forward = pronadjiBrid(graf, u, v);
	            forward.tok += protokNaPutu;

	            Brid backward = pronadjiBrid(graf, v, u);
	            if (backward == null) {
	                backward = new Brid(0, 0, v, u);//ako ne postoji dodaje novi povratni brid
	                graf.bridovi.add(backward);
	            }
	            backward.tok -= protokNaPutu;

	            v = u;
	        }

	        return protokNaPutu;
	    }

	    private Brid pronadjiBrid(Graf graf, int u, int v) {//trazenje bridova
	        for (Brid b : graf.bridovi) {
	            if (b.pocetniVrh == u && b.krajnjiVrh == v) {
	                return b;
	            }
	        }
	        return null;
	    }


	    private KorakDTO snimiStanje(Graf graf, String akcija, List<List<Integer>> put) {//snima stanje za simulaciju
	        KorakDTO korak = new KorakDTO();
	        korak.setAkcija(akcija);
	        korak.setPut(put);
	        
	        // Snimimo stanja bridova
	        List<StanjeBridDTO> bridoviDTO = new ArrayList<>();//bridovi 
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
			int[] roditelj = bfs(graf, izvor, ponor);
			if(roditelj == null) {
				break; //dok ima povecavajuceg puta, ako nema onda break
			}
			
			int protokNaPutu = slanjeToka(graf, roditelj, izvor, ponor); //minimalni rezidualni kapacitet na putu
			maksimalniTok += protokNaPutu;//slanje toka
			String opisKoraka = formirajPut(roditelj, izvor, ponor, protokNaPutu);//opis za simulaciju
			List<List<Integer>> put = generirajPutParove(roditelj, izvor, ponor);
			koraci.add(snimiStanje(graf, opisKoraka, put));//opis za simulaciju
			
		}
		
		return new SimulacijaDTO(koraci, maksimalniTok);
	}

}
