package data;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class Example implements Iterable<Double>{
	
	Float c;	
	private List <Double> example;
	 
	Example (){
		example=new LinkedList<>();
	}
	
	public Iterator<Double> iterator() {
		return example.iterator();
	}
	
	// si noti che il metodo “set” è stato rimpiazzato dal metodo “add”
	void add(Double v) {
		example.add(v);
	}
	
	Double get(int index){
		return example.get(index);
	}
	
	/* LEGACY
	public Double distance (Example newE) throws InvalidSizeException
	{
		Double distance = 0.0;
		if(newE.example.size() == example.size())
			for (int i = 0 ; i < example.size() ; i++)
				distance += Math.pow(example.get(i) - newE.example.get(i),2);
		
		else throw new InvalidSizeException(this,newE);
		
		return distance;
	}*/
	public Double distance (Example newE) throws InvalidSizeException{
		Double distance = 0.0;
		double e1;
		double e2;

		
		Iterator <Double> i1 = example.iterator();
		Iterator <Double> i2 = newE.example.iterator();

		
		if(newE.example.size() == example.size())
			while (i1.hasNext()) {
				e1 = i1.next();
				e2 = i2.next();
				distance += Math.pow(e1 - e2,2);

			}
		
		else throw new InvalidSizeException(this,newE);
		
		return distance;
	}

	public String toString()
	{
		String s = new String("");
		s = "[";
		for(int i=0;i<example.size();i++)
		{
			s+=String.valueOf(example.get(i));
			if(i == example.size() - 1)
				s+="]";
			else s+=",";
		}
		return s;
	}
}					 



