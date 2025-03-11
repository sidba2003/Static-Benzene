package src.main.java.com.lang.benzene.Interpreter;

import java.util.List;
import java.util.Map;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;

public class BenzeneClass implements BenzeneCallable {
    final String name;
    public Environment fields;
    public Environment methods;

    BenzeneClass(String name, Environment fields, Environment methods){
        this.name = name;
        this.fields = fields;
        this.methods = methods;
    }

    BenzeneFunction findMethod(String methodName){
        if (this.methods.values.containsKey(methodName)){
            return (BenzeneFunction) this.methods.values.get(methodName);
        }

        return null;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        BenzeneInstance instance = new BenzeneInstance(this);
        BenzeneFunction initializer = findMethod("init");
        if (initializer != null){
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity(){
        BenzeneFunction initializer = findMethod("init");

        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public String toString(){
        return "cls<" + name + ">";
    }
}
