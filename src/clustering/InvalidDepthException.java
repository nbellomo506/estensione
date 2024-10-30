package clustering;
import data.Data;
public class InvalidDepthException extends InstantiationException{
	public InvalidDepthException(String str){
		System.out.print("Profondità non valida, " + str);
	}
    public InvalidDepthException(Data data) {
		System.out.print("Profondità del dendrogramma non valida, deve essere <= " + data.getNumberOfExamples()+"\n");
    }

}
