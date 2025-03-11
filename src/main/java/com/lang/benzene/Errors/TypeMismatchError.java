package src.main.java.com.lang.benzene.Errors;

import java.lang.reflect.Type;

import src.main.java.com.lang.benzene.Tokens.Token;

public class TypeMismatchError extends RuntimeException {
    public Token token;

    public TypeMismatchError(Token token, String message){
        super(message);
        this.token = token;
    }

    public TypeMismatchError(String message){
        super(message);
    }
}
