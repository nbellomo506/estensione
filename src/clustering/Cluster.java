package clustering;
import data.Data;

import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.Serializable;
import java.util.HashSet;

public class Cluster implements Iterable<Integer>, Cloneable, Serializable {    

	private Set<Integer> clusteredData = new TreeSet<>();
	
	public Iterator <Integer> iterator() {
		return clusteredData.iterator();
	}
	
	void addData(int id) {
		clusteredData.add(id);
	}
		
	public int getSize() {
		return clusteredData.size();
	}

	@Override
	public Object clone() {
		Cluster copy = new Cluster();
		copy.clusteredData.addAll(this.clusteredData);
		return copy;
	}
	
	Cluster mergeCluster(Cluster c) {
		Cluster newC = new Cluster();
		newC.clusteredData.addAll(this.clusteredData);
		newC.clusteredData.addAll(c.clusteredData);
		return newC;
	}
	
	@Override
	public String toString() {		
		return clusteredData.toString();
	}
	
	String toString(Data data) {
		StringBuilder str = new StringBuilder();
		for (Integer id : clusteredData) {
			str.append("<").append(data.getExample(id)).append(">");                
		}
		return str.toString();
	}

	public Cluster() {
		clusteredData = new TreeSet<>();
	}

	public Cluster(int index) {
		clusteredData = new TreeSet<>();
		clusteredData.add(index);
	}

	public Cluster(Cluster other) {
		this.clusteredData = new TreeSet<>(other.clusteredData);
	}

	public void merge(Cluster other) {
		this.clusteredData.addAll(other.clusteredData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Cluster cluster = (Cluster) obj;
		return clusteredData.equals(cluster.clusteredData);
	}

	@Override
	public int hashCode() {
		return clusteredData.hashCode();
	}
}
