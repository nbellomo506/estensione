package clustering;
import data.Data;
import java.io.Serializable;

class Dendrogram implements Serializable {
	
	private ClusterSet[] tree; // modella il dendrogram
	
	Dendrogram(int depth) {
		tree = new ClusterSet[depth];
	}
	
	void setClusterSet(ClusterSet c, int level) {
		tree[level] = c;
	}
	
	ClusterSet getClusterSet(int level) {
		return tree[level]; 
	}
	
	int getDepth() {
		return tree.length;
	}
	
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < tree.length; i++) {
			if (tree[i] != null) {
				str += i + ":" + tree[i].toString() + "\n";
			}
		}
		return str;
	}

	public String toString(Data data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDepth(); i++) {
			sb.append("level").append(i).append(":\n");
			sb.append(tree[i].toString(data)).append("\n\n");
		}
		return sb.toString();
	} 
}
