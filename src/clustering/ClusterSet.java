package clustering;
import data.Data;
import distance.ClusterDistance;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

class ClusterSet implements Serializable{

	private List<Cluster> clusters;
	private transient int lastClusterIndex=0;
	
	ClusterSet(int k){
		clusters=new ArrayList<Cluster>(k);
	}
	
	ClusterSet(){
		clusters=new ArrayList<Cluster>();
	}
	
	void add(Cluster c){
		for(int j=0;j<lastClusterIndex;j++)
			if(c.equals(clusters.get(j))) // to avoid duplicates
				return;
		clusters.add(c);
		lastClusterIndex++;
	}
	
	ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) {
		if (clusters.size() <= 1) return new ClusterSet(this);

		int c1 = -1, c2 = -1;
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < clusters.size() - 1; i++) {
			for (int j = i + 1; j < clusters.size(); j++) {
				double currentDistance = distance.computeDistance(clusters.get(i), clusters.get(j), data);
				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					c1 = i;
					c2 = j;
				}
			}
		}

		ClusterSet newSet = new ClusterSet();
		Cluster merged = new Cluster(clusters.get(c1));
		merged.merge(clusters.get(c2));
		newSet.add(merged);

		for (int i = 0; i < clusters.size(); i++) {
			if (i != c1 && i != c2) {
				newSet.add(new Cluster(clusters.get(i)));
			}
		}

		return newSet;
	}
	
	Cluster get(int i){
		return clusters.get(i);
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(int i=0;i<clusters.size();i++){
			if (clusters.get(i)!=null){
				str.append("cluster").append(i).append(":").append(clusters.get(i)).append("\n");
		
			}
		}
		return str.toString();
		
	}
	
	String toString(Data data){
		StringBuilder str = new StringBuilder();
		for(int i=0;i<clusters.size();i++){
			if (clusters.get(i)!=null){
				str.append("cluster").append(i).append(":").append(clusters.get(i).toString(data)).append("\n");
		
			}
		}
		return str.toString();
		
	}
	
	// Aggiunto costruttore di copia
	ClusterSet(ClusterSet other) {
		this.clusters = new ArrayList<>(other.clusters);
		this.lastClusterIndex = other.lastClusterIndex;
	}
}
