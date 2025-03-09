package src.main.java.com.lang.benzene.Environment;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.ValueNotFoundError;
import src.main.java.com.lang.benzene.Tokens.Token;

public class Environment {
    public final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    public Environment(){
        enclosing = null;
    }

    public Environment(Environment enclosing){
        this.enclosing = enclosing;
    }

    public void define(String name, Object value){
        values.put(name, value);
    }

    public void assign(Token name, Object value){
        if (values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null){
            enclosing.assign(name, value);
            return;
        }

        throw new ValueNotFoundError(name, "Undefined variable " + name.lexeme + ".");
    }

    public Object get(Token name){
        if (values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        
        if (enclosing != null) return enclosing.get(name);
        throw new ValueNotFoundError(name, "Undefined variable " + name.lexeme + ".");
    }
}
