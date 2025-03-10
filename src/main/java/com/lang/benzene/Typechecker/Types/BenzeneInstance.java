package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.RuntimeError;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Tokens.Token;

public class BenzeneInstance extends Type {
    private BenzeneClass klass;
    private final Map<String, Type> fields = new HashMap<>();
    private final Map<String, Object> methods;

    public BenzeneInstance(BenzeneClass klass){
        super(klass.name);
        this.klass = klass;

        for (Map.Entry<String, Object> entry : this.klass.fields.values.entrySet()){
            fields.put(entry.getKey(), (Type) entry.getValue());
        }

        this.methods = this.klass.methods.values;
    }

    public String getName(){
        return this.klass.name;
    }

    public Object get(Token name){
        if (methods.containsKey(name.lexeme)){
            return methods.get(name.lexeme);
        } else if (fields.containsKey(name.lexeme)){
            return fields.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined proeprty " + name.lexeme + ".");
    }

    public void set(Token name, Type value){
        if (fields.containsKey(name.lexeme)){
            if (!(fields.get(name.lexeme).equals(value))){
                throw new TypeMismatchError(name, "Type of property being set does not match the type being set to.");
            }
        } else {
            throw new RuntimeError(name, "Undefined proeprty " + name.lexeme + ".");
        }
    }
}
