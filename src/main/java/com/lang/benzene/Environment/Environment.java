package src.main.java.com.lang.benzene.Environment;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.ValueNotFoundError;
import src.main.java.com.lang.benzene.Tokens.Token;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value){
        values.put(name, value);
    }

    public Object get(Token name){
        if (values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }

        throw new ValueNotFoundError(name, "Undefined variable " + name.lexeme + ".");
    }
}
