package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.RuntimeError;
import src.main.java.com.lang.benzene.Tokens.Token;

public class BenzeneInstance extends Type {
    private BenzeneClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    public BenzeneInstance(BenzeneClass klass){
        super(klass.name);
        this.klass = klass;

        for (Map.Entry<String, Object> entry : this.klass.fields.values.entrySet()){
            fields.put(entry.getKey(), entry.getValue());
        }
    }

    public String getName(){
        return this.klass.name;
    }

    public Object get(Token name){
        if (fields.containsKey(name.lexeme)){
            return fields.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined proeprty " + name.lexeme + ".");
    }
}
