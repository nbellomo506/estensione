package distance;
import java.util.Iterator;

import clustering.Cluster;
import data.Data;
import data.Example;

public class SingleLinkDistance implements ClusterDistance {
    public double computeDistance(Cluster c1, Cluster c2, Data d) {
        double min = Double.POSITIVE_INFINITY;
        
        for (Integer id1 : c1) {
            Example e1 = d.getExample(id1);
            for (Integer id2 : c2) {
                Example e2 = d.getExample(id2);
                try {
                    double distance = e1.distance(e2);
                    min = Math.min(min, distance);
                } catch (data.InvalidSizeException e) {
                    System.err.println("Errore nel calcolo della distanza: " + e.getMessage());
                }
            }
        }
        
        return min;
    }
}
