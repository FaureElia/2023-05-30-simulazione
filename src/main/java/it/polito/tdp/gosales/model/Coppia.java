package it.polito.tdp.gosales.model;

public class Coppia implements Comparable<Coppia> {
	private Retailers r1;
	private Retailers r2;
	private int peso ;
	
	
	public Coppia(Retailers r1, Retailers r2, int peso) {
		super();
		this.r1 = r1;
		this.r2 = r2;
		this.peso = peso;
	}
	public Retailers getR1() {
		return r1;
	}
	public void setR1(Retailers r1) {
		this.r1 = r1;
	}
	public Retailers getR2() {
		return r2;
	}
	public void setR2(Retailers r2) {
		this.r2 = r2;
	}
	public int getPeso() {
		return peso;
	}
	public void setPeso(int peso) {
		this.peso = peso;
	}
	
	@Override
	public int compareTo(Coppia o) {
		
		return this.peso-o.peso;
	}
	@Override
	public String toString() {
		return this.r1+"-"+this.r2+"["+this.peso+"]";
	}
	
	
	
	
	
	
	

}
