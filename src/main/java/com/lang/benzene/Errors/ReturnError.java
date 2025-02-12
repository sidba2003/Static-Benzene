package src.main.java.com.lang.benzene.Errors;

public class ReturnError extends RuntimeException {
    public final Object value;

    public ReturnError(Object value){
        super(null, null, false, false);
        this.value = value;
    }
    
}
