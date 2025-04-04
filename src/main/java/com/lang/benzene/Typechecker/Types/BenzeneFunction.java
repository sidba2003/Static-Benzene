package src.main.java.com.lang.benzene.Typechecker.Types;

import static src.main.java.com.lang.benzene.Tokens.TokenType.TYPE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;

public class BenzeneFunction extends Type implements BenzeneCallable {
    public List<String> parameterTypes;
    public String returnType;
    public List<Token> parameters;
    public List<Stmt> body;

    public static boolean insideFunction = false;
    public static ArrayList<Type> returnTypes = new ArrayList<>();

    private Stmt.Function declaration;
    private final Environment closure;

    public BenzeneFunction(Stmt.Function declaration,Environment closure, List<Token> params, List<String> paramTypes, String returnType, List<Stmt> body){
        super("function");

        this.declaration = declaration;
        this.parameters = params;
        this.parameterTypes = paramTypes;
        this.returnType = returnType;
        this.body = body;
        this.closure = closure;
    }

    public String getName(){
        StringBuilder functionType = new StringBuilder();
        
        functionType.append("fn<");
        functionType.append(this.returnType.substring(2, returnType.length() - 2) + "<");

        for (String paramType : this.parameterTypes){
            functionType.append(paramType.substring(2, paramType.length() - 2) + ",");
        }

        // we only remove the last comma if the last added char is a comma
        if (functionType.charAt(functionType.length() - 1) == ','){
            functionType.deleteCharAt(functionType.length() - 1);
        }
        
        functionType.append(">>");

        return functionType.toString();
    }

    @Override
    public int arity(){
        return parameters.size();
    }

    // only implemented the call function to incorporate java's rules
    // made some mistakes in the initial working days on this project :(
    @Override
    public Type call(Typechecker tc, List<Type> args){
        return Type.getTypeFromString(this.returnType);
    }

    @Override
    public Type typecheck(Typechecker typechecker){
        Environment environment = new Environment(this.closure);
        for (int i = 0; i < this.parameters.size(); i++){
            environment.define(this.parameters.get(i).lexeme, Type.getTypeFromString(this.parameterTypes.get(i)));
        }

        insideFunction = true;
        typechecker.executeBlock(this.body, environment);
        insideFunction = false;

        // checking the return types of all the return statements in the function is acceptable
        for (Type returnType : returnTypes){
            if (!returnType.equals(Type.getTypeFromString(this.returnType))){
                throw new TypeMismatchError(this.declaration.name, "Return type of function does not match the declared return type in " + this.declaration.name.lexeme + ".");
            } 
        }

        // removing all the encountered return types within the function
        returnTypes.clear();

        return (Type) Type.getTypeFromString(this.returnType);
    }

    @Override
    public void checkCall(ArrayList<Type> argumentTypes){
        for (int i = 0; i < argumentTypes.size(); i++){
            if (!argumentTypes.get(i).equals(this.parameterTypes.get(i))){
                throw new TypeMismatchError(this.declaration.name, "Type mismatch while trying to typecheck function call");
            }
        }
    }

    @Override
    public Type getReturnTypeString(){
        return null;
    }
}