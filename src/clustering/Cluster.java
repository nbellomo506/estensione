package clustering;
import data.Data;

import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.Serializable;
import java.util.HashSet;

public class Cluster implements Iterable<Integer>, Cloneable, Serializable {    

	private Set<Integer> clusteredData = new TreeSet<>(); // Insieme di dati raggruppati
	private double x; // Coordinata X
	private double y; // Coordinata Y
	private double width; // Larghezza
	private double height; // Altezza
	
	public Iterator <Integer> iterator() {
		return clusteredData.iterator(); // Restituisce un iteratore per i dati raggruppati
	}
	
	void addData(int id) {
		clusteredData.add(id); // Aggiunge un ID ai dati raggruppati
	}
		
	public int getSize() {
		return clusteredData.size(); // Restituisce la dimensione del cluster
	}

	@Override
	public Object clone() {
		Cluster copy = new Cluster(); // Crea una copia del cluster
		copy.clusteredData.addAll(this.clusteredData); // Copia i dati raggruppati
		return copy; // Restituisce la copia
	}
	
	Cluster mergeCluster(Cluster c) {
		Cluster newC = new Cluster(); // Crea un nuovo cluster
		newC.clusteredData.addAll(this.clusteredData); // Aggiunge i dati del cluster corrente
		newC.clusteredData.addAll(c.clusteredData); // Aggiunge i dati del cluster passato
		return newC; // Restituisce il nuovo cluster unito
	}
	
	@Override
	public String toString() {		
		return clusteredData.toString(); // Restituisce una rappresentazione stringa dei dati raggruppati
	}
	
	String toString(Data data) {
		StringBuilder str = new StringBuilder(); // Costruttore di stringhe per la rappresentazione
		for (Integer id : clusteredData) {
			str.append("<").append(data.getExample(id)).append(">"); // Aggiunge l'esempio associato all'ID
		}
		return str.toString(); // Restituisce la rappresentazione finale
	}

	public Cluster() {
		clusteredData = new TreeSet<>(); // Inizializza un nuovo insieme di dati raggruppati
	}

	public Cluster(int index) {
		clusteredData = new TreeSet<>(); // Inizializza un nuovo insieme di dati raggruppati
		clusteredData.add(index); // Aggiunge l'indice al cluster
	}

	public Cluster(Cluster other) {
		this.clusteredData = new TreeSet<>(other.clusteredData); // Crea un nuovo cluster copiando i dati da un altro cluster
	}

	public void merge(Cluster other) {
		this.clusteredData.addAll(other.clusteredData); // Unisce i dati di un altro cluster nel cluster corrente
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true; // Controlla se sono lo stesso oggetto
		if (obj == null || getClass() != obj.getClass()) return false; // Controlla se l'oggetto è nullo o di un tipo diverso
		Cluster cluster = (Cluster) obj; // Cast dell'oggetto a Cluster
		return clusteredData.equals(cluster.clusteredData); // Controlla l'uguaglianza dei dati raggruppati
	}

	@Override
	public int hashCode() {
		return clusteredData.hashCode(); // Restituisce il codice hash dei dati raggruppati
	}

	// Metodi per ottenere le coordinate e le dimensioni
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	// Costruttore
	public Cluster(int index, double x, double y, double width, double height) {
		this.clusteredData = new TreeSet<>();
		this.clusteredData.add(index);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
