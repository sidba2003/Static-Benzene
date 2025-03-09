package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;


public class BenzeneClass extends Type implements BenzeneCallable {
    final String name;
    public Environment fields;

    public BenzeneClass(String name, Environment fields){
        super(name);

        this.name = name;
        this.fields = fields;
    }

    public String getName(){
        return "cls<" + name + ">";
    }

    @Override
    public Object call(Typechecker interpreter, List<Object> arguments){
        BenzeneInstance instance = new BenzeneInstance(this);
        return instance;
    }

    @Override
    public int arity(){
        return 0;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public Object typecheck(Typechecker tc){
        return null;
    }

    @Override
    public void checkCall(ArrayList<Type> types){
        return;
    }

    @Override
    public Type getReturnTypeString(){
        return null;
    }
}
