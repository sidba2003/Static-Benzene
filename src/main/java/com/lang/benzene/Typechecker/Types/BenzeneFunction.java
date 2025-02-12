package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.List;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;

public class BenzeneFunction extends Type implements BenzeneCallable {
    public List<String> parameterTypes;
    public String returnType;
    public List<Token> parameters;
    public List<Stmt> body;

    public BenzeneFunction(List<Token> params, List<String> paramTypes, String returnType, List<Stmt> body){
        super("function");

        this.parameters = params;
        this.parameterTypes = paramTypes;
        this.returnType = returnType;
        this.body = body;
    }

    public String getName(){
        // need to implement this
    }

    @Override
    public int arity(){
        return parameters.size();
    }

    @Override
    public Type call(Typechecker typechecker){
        Environment environment = new Environment(typechecker.globals);
        for (int i = 0; i < this.parameters.size(); i++){
            environment.define(this.parameters.get(i).lexeme, Type.getTypeFromString(this.parameterTypes.get(i)));
        }

        typechecker.executeBlock(this.body, environment);
        return null;
    }
}
