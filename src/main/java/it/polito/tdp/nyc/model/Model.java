package it.polito.tdp.nyc.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	NYCDao dao;
	Graph<String,DefaultWeightedEdge> grafo;
	
	public Model() {
		dao=new NYCDao();
		
	}
	
	
	public void creaGrafo(String borough) {
		grafo = new SimpleWeightedGraph <String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//List<String> listaVertici = dao.creaVertici(borough);
		//Graphs.addAllVertices(listaVertici);
		
		List<NTAconSSID> listaVertici = dao.creaArchi(borough);
		for(NTAconSSID nta : listaVertici)  {
			grafo.addVertex(nta.getNTA());
		}
			
		List<NTAconSSID> listaArchi=dao.creaArchi(borough);
		
			for(NTAconSSID a1 : listaArchi) {
			
				for (NTAconSSID a2 : listaArchi){
				
					if(! a1.getNTA().equals(a2.getNTA())) {
					
						Set<String> unioneSSID = new HashSet<String>(a1.getSSID());
						unioneSSID.addAll(a2.getSSID());
						
						// Nelle due righe di codice precedenti ho unito i Set<String> dei due cicli for()
						// infatti il metodo addAll() è in grado di aggiungere ad un Set/List
						// gli elementi dell'altro Set/List che viene passato come parametro 
						// senza aggiungere elementi gia presenti nella lista di partenza,
						// ovvero senza creare duplicati nella lista
						
						double pesoArco= unioneSSID.size();
						
						Graphs.addEdgeWithVertices(grafo, a1.getNTA(), a2.getNTA(), pesoArco);
						
					}
				}
			}
		}
	
	
	
	public List<Arco> getArchiMaggioriPesoMedio() {
		
		double sommaPesoArchi=0;
		double pesoMedio=0;
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			sommaPesoArchi= sommaPesoArchi + grafo.getEdgeWeight(e);
		}
		pesoMedio = sommaPesoArchi / grafo.edgeSet().size();
		
		List<Arco> AchiMaggioriPesoMedio = new ArrayList<>();
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>pesoMedio) {
				Arco arco = new Arco(grafo.getEdgeSource(e),grafo.getEdgeTarget(e),grafo.getEdgeWeight(e));
				AchiMaggioriPesoMedio.add(arco);
			}
		}
		
		return AchiMaggioriPesoMedio;
	}
	
	
	public int getNumeroVertici() {
		if(grafo!=null) {
			return grafo.vertexSet().size();
		}
		else {
			return 0;	
		}
	}
	
	public int getNumeroArchi() {
		if(grafo!=null) {
			return grafo.edgeSet().size();
		}
		else {
			return 0;	
		}
	}

	public boolean grafoCreato() {
		if(grafo!=null) {
			return true;
		}
		else
		return false;
	}
	
	
	public List<String> getAllBorough() {
		return dao.getBorough();
	}
	
	
	
	
	// metodo Model del Simulatore
	public Map<String,Integer> Simula(double probabilitaCondivisione , int durataCondivisione) {
		
		Simulazione sim = new Simulazione( probabilitaCondivisione, durataCondivisione, grafo);
		sim.inizializzazione();
		sim.run();
		
		return sim.getNumeroTOTALECondivisioneMAP();
		
	}
	
	
	
	
	
	
	
}
