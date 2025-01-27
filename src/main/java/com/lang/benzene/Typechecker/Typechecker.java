package src.main.java.com.lang.benzene.Typechecker;

import src.main.java.com.lang.benzene.TreeNodes.Expr;
import static src.main.java.com.lang.benzene.Tokens.TokenType.*;
import src.main.java.com.lang.benzene.Typechecker.Types.Type;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Benzene;

public class Typechecker implements Expr.Visitor<Object> {
    public void typecheck(Expr expression){
        try {
            evaluate(expression);
        } catch (TypeMismatchError error){
            Benzene.typecheckError(error);
        }
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
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck binary expressions");
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
                throw new TypeMismatchError(expr.operator, "Type mismatch while trying to typecheck binary expressions");
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return Type.bool;
            default:
                return null;    // unreachable
        }
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }
}
