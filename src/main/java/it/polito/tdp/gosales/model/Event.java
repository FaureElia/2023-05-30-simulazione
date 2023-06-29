package it.polito.tdp.gosales.model;

import java.time.LocalDate;

public class Event implements Comparable<Event> {
	
	public enum EventType{
		RIFORNIMENTO,ACQUISTO;
	}
	
	//acquisto
	private LocalDate data;
	private EventType tipo;
	
	

	public Event(LocalDate data, EventType tipo) {
		super();
		this.data = data;
		this.tipo=tipo;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	@Override
	public int compareTo(Event o) {
		return this.data.compareTo(o.data);
	}

	public EventType getTipo() {
		return tipo;
	}

	public void setTipo(EventType tipo) {
		this.tipo = tipo;
	}
	
	
	

}
