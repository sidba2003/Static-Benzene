package src.main.java.com.lang.benzene.Typechecker;

import src.main.java.com.lang.benzene.TreeNodes.Expr;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;

import static src.main.java.com.lang.benzene.Tokens.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneFunction;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneInstance;
import src.main.java.com.lang.benzene.Typechecker.Types.Type;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;
import src.main.java.com.lang.benzene.Errors.BreakError;
import src.main.java.com.lang.benzene.Errors.ContinueError;
import src.main.java.com.lang.benzene.Errors.ReturnError;
import src.main.java.com.lang.benzene.Errors.RuntimeError;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Errors.ValueNotFoundError;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Benzene;
import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneClass;

public class Typechecker implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    public final Environment globals = new Environment();
    private Environment environment = globals;
    
    private int insideLoop = 0; // to keep track of nested loops

    public void typecheck(List<Stmt> statements){
        try {
            for (Stmt stmt : statements){
                execute(stmt);
            }
        } catch (TypeMismatchError | ValueNotFoundError error){
            if (error instanceof TypeMismatchError) Benzene.typecheckError((TypeMismatchError) error);
            if (error instanceof ValueNotFoundError) Benzene.typecheckError((ValueNotFoundError) error);
        }
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        Object value = Type.nil;
        if (stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        Type actualValue = (Type) value;
        if (!actualValue.equals(stmt.type)){
            throw new TypeMismatchError(stmt.name, "Type mismatch encountered during variable declaration.");
        }

        environment.define(stmt.name.lexeme, actualValue);
        return null;
    }

    /**
     * Checks if the functions defined in classes are valid or not
     * functions cannot have the name 'this', or be called 'init' and return something other than the class instance
     * @param methods
    * @throws Exception 
    */
    private void checkClassMethodsValidity(String className, List<Stmt> methods){
        for (Stmt function : methods){
            Stmt.Function functionCast = (Stmt.Function) function;
            if (functionCast.name.lexeme.equals("this")){
                throw new RuntimeException("Invalid method defined in the class");
            } else if (functionCast.name.lexeme.equals("init") && !functionCast.returnType.equals("<<" + className + ">>")){
                throw new RuntimeException("Invalid method defined in the class");
            }
        }
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt){
        // checks if all the methods defined in the class are valid or not
        checkClassMethodsValidity(stmt.name.lexeme, stmt.methods);

        Environment fieldsEnvironment = new Environment();
        executeBlock(stmt.variables, fieldsEnvironment);

        Environment methodsEnvironment = new Environment();
        
        BenzeneClass klass = new BenzeneClass(stmt.name.lexeme, fieldsEnvironment, methodsEnvironment);
        environment.define(stmt.name.lexeme, null);
        environment.assign(stmt.name, klass);

        BenzeneInstance instance = (BenzeneInstance) klass.call(null, null);

        methodsEnvironment.define("this", instance);
        Type.updateTypeMap(stmt.name.lexeme, instance);

        executeBlock(stmt.methods, klass.methods);

        Type.updateTypeMap(klass.getName(), klass);

        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        // disallow nested functions
        for (Stmt bodyStmt : stmt.body){
            if (bodyStmt instanceof Stmt.Function){
                Stmt.Function functionStmt = (Stmt.Function)bodyStmt;
                throw new TypeMismatchError(functionStmt.name, "Nested functions are not allowed in Benzene.");
            }
        }
        
        // if there doesnt exist a return statement in the topmost level of the function....
        // we add an implicit return statement at the end of the function body
        boolean hasReturn = false;
        for (Stmt bodyStmt : stmt.body){
            if (bodyStmt instanceof Stmt.Return){
                hasReturn = true;
                break;
            }
        }
        
        // adding the implicit return statement
        if (!hasReturn){
            stmt.body.add(new Stmt.Return(stmt.name, new Expr.Literal(new Token(NIL, "nil", null, 0))));
        }

        BenzeneFunction function = new BenzeneFunction(stmt, environment, stmt.params, stmt.paramTypes, stmt.returnType, stmt.body);
        environment.define(stmt.name.lexeme, function);

        Type.updateTypeMap(function.getName(), function);

        // we typecheck the function immediately after defining it
        function.typecheck(this);

        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        if (BenzeneFunction.insideFunction){
            BenzeneFunction.returnTypes.add((Type) evaluate(stmt.value));
            return null;
        } 
        throw new ReturnError(stmt.keyword);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        // no need to typecheck the condition, as all expressions in Benzene evaluate to true or false
        insideLoop++;
        execute(stmt.body);
        insideLoop--;

        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt){
        if (insideLoop == 0){
            throw new ContinueError(stmt.continueToken, "Continue statement encountered on line " + stmt.continueToken.line + ".");
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt){
        if (insideLoop == 0){
            throw new BreakError(stmt.breakToken, "Break statement encountered on line " + stmt.breakToken.line + ".");
        }
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        return environment.get(expr.name);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt){
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt){
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt){
        // all expressions in Benzene evaluate to true or false
        // so we can ignore the conditional expression and only typecheck the branches
        execute(stmt.thenBranch);
        if (stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr){
        Object assignValueType = evaluate(expr.value);
        Object variableType = environment.get(expr.name);

        if (!assignValueType.equals(variableType)){
            throw new TypeMismatchError(expr.name, "Type mismatch encountered during variable assignment.");
        }

        return assignValueType;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        switch (expr.literalToken.type) {
            case NUMBER:
                return Type.number;
            case STRING:
                return Type.string;
            case NIL:
                return Type.nil;
            case TRUE:
            case FALSE:
                return Type.bool;
            default:
                return null;    // this is unreachable, but adding it due to java's mandatory return type requirement
        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Type expressionType = (Type) evaluate(expr.right);

        switch (expr.operator.type){
            case MINUS:
                if (expressionType.equals(Type.number)){
                    return Type.number;
                }
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck unary expression");
            case BANG:
                // we simply return bool type in case of a '!', as every expression in Benzen evaluates to true or false
                return Type.bool;
            default:
                return null;
        }
    }

    @Override
    public Object visitGetExpr(Expr.Get expr){
        Object object = evaluate(expr.object);
        if (object instanceof BenzeneInstance){
            return ((BenzeneInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name, "Only instances have properties.");
    }

    @Override
    public Object visitSetExpr(Expr.Set expr){
        Object object = evaluate(expr.object);

        if (!(object instanceof BenzeneInstance)){
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((BenzeneInstance) object).set(expr.name, (Type) value);

        return value;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr){
        Object callee = evaluate(expr.callee);
        if (!(callee instanceof BenzeneCallable)){
            throw new TypeMismatchError(expr.paren, "Type mismatch while trying to typecheck function call");
        }

        ArrayList<Type> argumentTypes = new ArrayList<>();
        for (Expr argument : expr.arguments){
            argumentTypes.add((Type) evaluate(argument));
        }

        BenzeneCallable callable = (BenzeneCallable) callee;

        if (argumentTypes.size() != callable.arity()){
            throw new RuntimeException("Expected " + callable.arity() + " arguments but got " + argumentTypes.size() + ".");
        }

        try {
            callable.checkCall(argumentTypes);
        } catch (TypeMismatchError mismatchError){
            throw new TypeMismatchError(expr.paren, mismatchError.getMessage());
        }
        
        return callable.call(this, argumentTypes);
    }

    @Override 
    public Object visitBinaryExpr(Expr.Binary expr){
        Type left = (Type) evaluate(expr.left);
        Type right = (Type) evaluate(expr.right);

        switch (expr.operator.type) {
            case OR:
            case AND:
                // we simply return bool type in case of a 'OR' or 'AND', as every expression in Benzen evaluates to true or false
                return Type.bool;
            case MINUS:
                if (left.equals(Type.number) && (right.equals(Type.number))){
                    return Type.number;
                }
            case SLASH:
                if (left.equals(Type.number) && (right.equals(Type.number))){
                    return Type.number;
                }
            case STAR:
                if (left.equals(Type.number) && (right.equals(Type.number))){
                    return Type.number;
                }
                // this line will execute in case for all of MINUS, SLASH AND STAR
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck binary expression");
            case PLUS:
                if (left.equals(Type.number) && right.equals(Type.number)){
                    return Type.number;
                }
                if (left.equals(Type.string) && right.equals(Type.string)){
                    return Type.string;
                }
                if (left.equals(Type.string) && right.equals(Type.number)){
                    return Type.string;
                }
                if (left.equals(Type.number) && right.equals(Type.string)){
                    return Type.string;
                }
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck binary expressions");
            case GREATER:
                if (left.equals(Type.number) && right.equals(Type.number)){
                    return Type.number;
                }
            case GREATER_EQUAL:
                if (left.equals(Type.number) && right.equals(Type.number)){
                    return Type.number;
                }
            case LESS:
                if (left.equals(Type.number) && right.equals(Type.number)){
                    return Type.number;
                }
            case LESS_EQUAL:
                if (left.equals(Type.number) && right.equals(Type.number)){
                    return Type.number;
                }
                // this line will execute in case for all of, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck binary expression");
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                if (left.equals(Type.bool) && right.equals(Type.bool)){
                    return Type.bool;
                }
                // this line will execute for both of BANG_EQUAL AND EQUAL_EQUAL swicth statements
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck '==' or '!=' expression");
            default:
                return null;    // unreachable
        }
    }

    public void executeBlock(List<Stmt> statements, Environment environment){
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements){
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private void execute(Stmt stmt){
        stmt.accept(this);
    }
}
