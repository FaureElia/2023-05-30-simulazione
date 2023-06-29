package it.polito.tdp.gosales.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.gosales.dao.GOsalesDAO;
import it.polito.tdp.gosales.model.Event.EventType;

public class Simulazione {
	private GOsalesDAO dao;
	//input
	private int anno;
	private Retailers ret;
	private Products prod;
	private int N;
	private int Q;
	private double costoUnitario;
	private int avgD;
	private int avgQ;
	private LocalDate termine;
	private double probabilita;
	
	//coda eventi
	PriorityQueue<Event> queue;
	
	//stato del sistema
	private int disponibile;
	private LocalDate giorno;
	int insoddisfatti=0;
	int clientiTotali=0;
	
	//output
	private double spesa_totale;
	private double ricavo_totale;
	
	public Simulazione(Retailers ret, Products prod, int n, int q,int anno, int C) {
		this.ret = ret;
		this.prod = prod;
		N = n;
		Q = q;
		this.anno=anno;
		this.dao=new GOsalesDAO();
		this.costoUnitario=this.prod.isUnit_cost();
		int numero_acquisti=this.dao.getAcquisti(ret,prod,anno);
		this.avgD=((int)(12*30/numero_acquisti));
		
		this.avgQ=(int)(this.dao.getQuantita(ret,prod,anno)/numero_acquisti);
		
		this.queue=new PriorityQueue();
		this.spesa_totale=0;
		this.ricavo_totale=0;
		this.termine=LocalDate.of(this.anno, 12, 31);
		
		this.probabilita=Math.min(20+1*C,50)/100;
		
		
	}

	public void initialize() {
		this.disponibile=this.Q;
		//rifornimenti
		System.out.println("inizializzo");
		LocalDate giorno=LocalDate.of(this.anno, 1, 1);
		while(giorno.isBefore(this.termine)) {
			this.queue.add(new Event(giorno,EventType.RIFORNIMENTO));
			giorno=giorno.plus(1, ChronoUnit.MONTHS);
			
		}
		System.out.println("rifrnimenti inizializzati");
		
		//acquisti
		giorno=LocalDate.of(this.anno, 1, 15);
		while(giorno.isBefore(this.termine)) {
			this.queue.add(new Event(giorno,EventType.ACQUISTO));
			giorno=giorno.plus(this.avgD,ChronoUnit.DAYS);	
		}
		System.out.println("acquisti inizializzati");
		//fine inizializzazione	
	}

	public void run() {

		while(!this.queue.isEmpty()) {
			System.out.println("---------------------------");
			System.out.println("Cliente compra, quantità disponibili: "+this.disponibile);
			Event e=this.queue.poll();
			LocalDate giorno=e.getData();
			EventType tipo= e.getTipo();
			switch(tipo) {
			case RIFORNIMENTO:
				if(Math.random()<this.probabilita) {
					int consegnata=(int)(this.N*0.8);
					this.disponibile+=consegnata;
					this.spesa_totale=this.costoUnitario*consegnata;
				}else {
					 int consegnata=(int)(this.N);
					 this.disponibile+=consegnata;
					this.spesa_totale=this.costoUnitario*consegnata;
				}
				break;
			case ACQUISTO:
				System.out.println("Cliente compra, quantità desiderata: "+this.avgQ);
				this.clientiTotali++;
				if(this.disponibile>this.avgQ) {
					this.disponibile-=avgQ;
					this.ricavo_totale=this.prod.isUnit_price()*avgQ;
					
				}else if(this.disponibile<this.avgQ) {
					this.disponibile=0;
					this.ricavo_totale=this.prod.isUnit_price()*this.disponibile;
					if(this.disponibile<0.9*this.avgQ) {
						this.insoddisfatti++;
					}
				}
				break;
			}
			
		
		
		}
		
		System.out.println("insoddisfatti: "+this.insoddisfatti);

		System.out.println("totale serviti: "+this.clientiTotali);
		
		System.out.println("spesa totale: "+this.spesa_totale);
		System.out.println("ricavo totale: "+this.ricavo_totale);
		
	}
	
	
	
	
	
	
	
	
	
	

}
