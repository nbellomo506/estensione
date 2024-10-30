package clustering;
import data.Data;
import distance.ClusterDistance;
import distance.SingleLinkDistance;

import java.io.*;

public class HierachicalClusterMiner implements Serializable{
	
	private Dendrogram dendrogram;
	private transient ClusterDistance distanceUsed;  // Nuovo campo

	public static HierachicalClusterMiner loadHierachicalClusterMiner(String fileName)
			throws FileNotFoundException,IOException,ClassNotFoundException{
		
		FileInputStream fi = null;
		ObjectInputStream oi = null;
		HierachicalClusterMiner hcm = null;

		fi = new FileInputStream(fileName);
		oi = new ObjectInputStream(fi);
		hcm = (HierachicalClusterMiner)oi.readObject(); 
		oi.close();

		return hcm;
	
	}
	
	public void salva(String fileName) throws IOException {
		
		FileOutputStream fo = null;
		ObjectOutputStream os = null;

		fo = new FileOutputStream(fileName);
		os = new ObjectOutputStream(fo);
		os.writeObject(this); 
		os.close();
		
	}
	
	public HierachicalClusterMiner(int depth) throws NegativeDepthException{
		if(depth <= 0)
			throw new NegativeDepthException("la profondità deve essere > 0\n");
		dendrogram = new Dendrogram(depth);
	
	}
	
	public void mine(Data data, ClusterDistance distance) throws NegativeDepthException,InvalidDepthException {
		this.distanceUsed = distance;
				
		if (dendrogram.getDepth() > data.getNumberOfExamples())
			throw new InvalidDepthException(data);
		
		if (dendrogram.getDepth() < 1)
			throw new NegativeDepthException(null);
		
		ClusterSet initialSet = new ClusterSet(data.getNumberOfExamples());
		for(int i = 0; i < data.getNumberOfExamples(); i++) {
			Cluster cluster = new Cluster(i);
			initialSet.add(cluster);
		}
		dendrogram.setClusterSet(initialSet, 0);
		
		for(int i = 1; i < dendrogram.getDepth(); i++) {
			ClusterSet previousSet = dendrogram.getClusterSet(i - 1);
			ClusterSet newSet = previousSet.mergeClosestClusters(distance, data);
			dendrogram.setClusterSet(newSet, i);
		}
	}
	
    @Override
    public String toString() {
        return dendrogram.toString();
    }
    
    public String toString(Data data) {
        return "Distanza utilizzata: " + (distanceUsed instanceof SingleLinkDistance ? "Single Link" : "Average Link") + "\n" + dendrogram.toString(data);
    }
    
    public int getDepth() {
        return dendrogram.getDepth();
    }

    public void setDepth(int depth) throws NegativeDepthException {
    	if (depth <= 0) {
    		throw new NegativeDepthException("la profondità deve essere > 0\n");
    	}
    	dendrogram = new Dendrogram(depth);
 }
}
