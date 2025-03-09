package src.main.java.com.lang.benzene.Interpreter;

import java.util.List;
import java.util.Map;

import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;

public class BenzeneClass implements BenzeneCallable {
    final String name;

    BenzeneClass(String name){
        this.name = name;
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
