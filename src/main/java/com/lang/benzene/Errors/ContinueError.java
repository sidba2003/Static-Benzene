package src.main.java.com.lang.benzene.Errors;

import src.main.java.com.lang.benzene.Tokens.Token;

public class ContinueError extends RuntimeException {
    public Token token;
    
    public ContinueError(Token token, String message){
        super(message);
        this.token = token;
    }
}
