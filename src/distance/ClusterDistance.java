package distance;

import clustering.Cluster;
import data.Data;

public interface ClusterDistance {
	double computeDistance(Cluster c1, Cluster c2, Data data);
}
