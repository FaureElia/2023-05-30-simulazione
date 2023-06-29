package it.polito.tdp.gosales.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.gosales.dao.GOsalesDAO;

public class Model {
	
	private GOsalesDAO dao;
	private Graph<Retailers, DefaultWeightedEdge> grafo;
	private Map<Integer, Retailers> idMap;
	private List<Coppia> coppie;
	private Integer sommaPesi;
	
	
	public Model() {
		this.dao=new GOsalesDAO();
		
	}

	public List<String> getCountries() {
		
		return this.dao.getAllCountries();
	}

	public List<Retailers> creaGrafo(Integer anno, String nazione, Integer m) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap=new HashMap<>();
		
		List<Retailers> vertici=this.dao.getRetailers(nazione);
		
		
		Graphs.addAllVertices(this.grafo, vertici);
		
		for(Retailers r: this.grafo.vertexSet()) {
			this.idMap.put(r.getCode(),r);
			
		}
		
		System.out.println(this.grafo.vertexSet().size());
		
//		this.coppie=this.dao.getCoppie(anno,nazione,m,this.idMap);
//		for(Coppia c: coppie) {
//			Graphs.addEdgeWithVertices(this.grafo, c.getR1(), c.getR2(), c.getPeso());
//		}
		//alternativa per calcolare i vertici, considero ogni coppia:
		this.coppie=new ArrayList<>();
		for (Retailers r1: this.grafo.vertexSet()) {
			for(Retailers r2: this.grafo.vertexSet()) {
				if(r1.getCode()>r2.getCode() && !this.grafo.containsEdge(this.grafo.getEdge(r2, r1))) {
					Coppia c=this.dao.getProdottiCondivisi(r1,r2,anno);
					if(c.getPeso()>=m) {
						Graphs.addEdgeWithVertices(this.grafo, r1, r2, c.getPeso());
						this.coppie.add(c);
						
					}
				}
			}
		}
		
		return vertici;
		
		
	}
	
	public List<Coppia> getArchi() {
		Collections.sort(this.coppie);
		return coppie;
	}

	public Integer calcolaComponente(Retailers r) {
		if(this.grafo==null || this.grafo.vertexSet().size()==0) {
			return null;
		}
		
		ConnectivityInspector<Retailers, DefaultWeightedEdge> inspector=new ConnectivityInspector<>(this.grafo);
		Set<Retailers> compConnessa=inspector.connectedSetOf(r);
		this.sommaPesi=0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(compConnessa.contains(this.grafo.getEdgeSource(e)) && compConnessa.contains(this.grafo.getEdgeTarget(e)) ) {
				this.sommaPesi+=(int)this.grafo.getEdgeWeight(e);
			}
		}
		
		
		return compConnessa.size();
		
		
	}
	
	public int getSommaPesi() {
		return this.sommaPesi;
		
	}

	public List<Products> getProdotti(Retailers r,int anno) {
		return this.dao.getProdotti(r,anno);
		
	}

	public void simula(Retailers r, Products p, int n, int q,int anno) {
		Simulazione sim=new Simulazione(r,p,n,q,anno,this.calcolaComponente(r)-1);
		
		sim.initialize();
		sim.run();
		
	}
}
