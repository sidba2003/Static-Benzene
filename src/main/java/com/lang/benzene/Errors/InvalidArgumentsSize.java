package src.main.java.com.lang.benzene.Errors;

public class InvalidArgumentsSize extends RuntimeException{
    private String message;

    public InvalidArgumentsSize(String message){
        super(message);
        this.message = message;
    }
}
