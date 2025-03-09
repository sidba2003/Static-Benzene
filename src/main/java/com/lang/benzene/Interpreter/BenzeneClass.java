package src.main.java.com.lang.benzene.Interpreter;

import java.util.List;
import java.util.Map;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;

public class BenzeneClass implements BenzeneCallable {
    final String name;
    public Environment fields;

    BenzeneClass(String name, Environment fields){
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        BenzeneInstance instance = new BenzeneInstance(this);
        return instance;
    }

    @Override
    public int arity(){
        return 0;
    }

    @Override
    public String toString(){
        return "cls<" + name + ">";
    }
}
