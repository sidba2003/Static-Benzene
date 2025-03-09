package src.main.java.com.lang.benzene.Errors;

import src.main.java.com.lang.benzene.Tokens.Token;

public class RuntimeError extends RuntimeException {
    public final Token name;
    public final String message;

    public RuntimeError(Token name, String message){
        super(message);

        this.name = name;
        this.message = message;
    }
}
