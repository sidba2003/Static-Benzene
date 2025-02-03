package src.main.java.com.lang.benzene.Errors;

import java.lang.module.ResolutionException;

import src.main.java.com.lang.benzene.Tokens.Token;

public class ValueNotFoundError extends RuntimeException{
    public Token token;

    public ValueNotFoundError(Token token, String message){
        super(message);
        this.token = token;
    }
}
