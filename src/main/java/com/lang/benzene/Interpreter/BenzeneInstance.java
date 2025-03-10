package src.main.java.com.lang.benzene.Interpreter;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.RuntimeError;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Tokens.Token;

public class BenzeneInstance {
    private BenzeneClass klass;
    private final Map<String, Object> fields = new HashMap<>();
    private final Map<String, Object> methods;

    BenzeneInstance(BenzeneClass klass){
        this.klass = klass;

        for (Map.Entry<String, Object> entry : this.klass.fields.values.entrySet()){
            fields.put(entry.getKey(), entry.getValue());
        }

        this.methods = this.klass.methods.values;
    }

    @Override
    public String toString(){
        return klass.name;
    }

    public Object get(Token name){
        if (this.methods.containsKey(name.lexeme)){
            BenzeneFunction function = (BenzeneFunction) this.methods.get(name.lexeme);
            return function.bind(this);
        }

        return fields.get(name.lexeme);
    }

    public void set(Token name, Object value){
        fields.put(name.lexeme, value);
    }
}
