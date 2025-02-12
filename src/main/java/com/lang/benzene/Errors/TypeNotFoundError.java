package src.main.java.com.lang.benzene.Errors;

public class TypeNotFoundError extends RuntimeException{
    String message;
    
    public TypeNotFoundError(String message){
        super(message);
        this.message = message;
    }
}
