package src.main.java.com.lang.benzene.Errors;

import src.main.java.com.lang.benzene.Tokens.Token;

public class BreakError extends RuntimeException {
    public Token token;

    public BreakError(Token token, String message){
        super(message);
        this.token = token;
    }
}
