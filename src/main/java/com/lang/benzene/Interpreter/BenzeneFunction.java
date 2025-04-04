package src.main.java.com.lang.benzene.Interpreter;

import java.util.List;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Errors.ReturnError;
import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;

class BenzeneFunction implements BenzeneCallable{
    private final Stmt.Function declaration;
    private final Environment closure;

    public BenzeneFunction(Stmt.Function declaration, Environment closure){
        this.declaration = declaration;
        this.closure = closure;
    }

    public BenzeneFunction bind(BenzeneInstance instance){
        Environment environment = new Environment(this.closure);
        environment.define("this", instance);
        return new BenzeneFunction(declaration, environment);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        Environment environment = new Environment(this.closure);
        for (int i = 0; i < declaration.params.size(); i++){
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnError returnValue){
            return returnValue.value;
        }
        
        return null;
    }

    @Override
    public int arity(){
        return declaration.params.size();
    }
}