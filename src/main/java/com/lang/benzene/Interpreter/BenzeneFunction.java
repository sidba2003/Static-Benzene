package src.main.java.com.lang.benzene.Interpreter;

import java.util.List;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Errors.ReturnError;
import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;

class BenzeneFunction implements BenzeneCallable{
    private final Stmt.Function declaration;

    public BenzeneFunction(Stmt.Function declaration){
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        Environment environment = new Environment(interpreter.globals);
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