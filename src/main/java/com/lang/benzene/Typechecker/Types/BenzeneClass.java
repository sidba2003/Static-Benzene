package src.main.java.com.lang.benzene.Typechecker.Types;

import static src.main.java.com.lang.benzene.Tokens.TokenType.STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.InvalidArgumentsSize;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;


public class BenzeneClass extends Type implements BenzeneCallable {
    final String name;
    public Environment fields;
    public Environment methods;

    public BenzeneClass(String name, Environment fields, Environment methods){
        super(name);

        this.name = name;
        this.fields = fields;
        this.methods = methods;
    }

    public String getName(){
        return "cls<" + name + ">";
    }

    BenzeneFunction findMethod(String name){
        if (this.methods.values.containsKey(name)){
            return (BenzeneFunction) this.methods.values.get(name);
        }

        return null;
    }

    @Override
    public Object call(Typechecker typechecker, List<Type> arguments){
        BenzeneInstance instance = new BenzeneInstance(this);

        BenzeneFunction intializer = findMethod("init");

        if (intializer != null){
            List<String> initializerTypes = intializer.parameterTypes;

            for (int i = 0; i < arguments.size(); i++){
                if (!arguments.get(i).equals(initializerTypes.get(i))){
                    // the token values are just placeholder values!!!! and not accurate just to get the error working in this place!
                    throw new TypeMismatchError(new Token(STRING, name, initializerTypes, i), "Types dont match for class instance creation.");
                }
            }
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
