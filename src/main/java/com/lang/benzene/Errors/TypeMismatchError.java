package src.main.java.com.lang.benzene.Errors;

import src.main.java.com.lang.benzene.Tokens.Token;

public class TypeMismatchError extends RuntimeException {
    public Token token;

    public TypeMismatchError(Token token, String message){
        super(message);
        this.token = token;
    }
}
