package src.main.java.com.lang.benzene.Typechecker;

import src.main.java.com.lang.benzene.TreeNodes.Expr;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;

import static src.main.java.com.lang.benzene.Tokens.TokenType.*;

import java.util.List;

import src.main.java.com.lang.benzene.Typechecker.Types.Type;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Errors.ValueNotFoundError;
import src.main.java.com.lang.benzene.Benzene;
import src.main.java.com.lang.benzene.Environment.Environment;

public class Typechecker implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

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
    public Object visitBinaryExpr(Expr.Binary expr){
        Type left = (Type) evaluate(expr.left);
        Type right = (Type) evaluate(expr.right);

        switch (expr.operator.type) {
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

    private void executeBlock(List<Stmt> statements, Environment environment){
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
