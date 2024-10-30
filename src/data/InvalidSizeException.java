package data;

public class InvalidSizeException extends Exception{
	
	InvalidSizeException(Example e1,Example e2){
		super(e1.toString() +" ha lunghezza diversa da " + e2.toString());
	}
	
}
