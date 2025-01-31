package src.main.java.com.lang.benzene.Interpreter;

import src.main.java.com.lang.benzene.TreeNodes.Expr;

public class Interpreter implements  Expr.Visitor<Object> {
    public void interpret(Expr expression){
        Object value = evaluate(expression);
        System.out.println(stringify(value));
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        switch (expr.literalToken.type){
            case NUMBER:
            case STRING:
                return expr.literalToken.literal;
            case TRUE:
                return true;
            case FALSE:
                return false;
            case NIL:
                return null;
            default:
                return null;    // this is unreachable code, but adding it due to java's requirements
        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case MINUS:
                return -(double)right;
            case BANG:
                return !isTruthy(right);
            default:
                return null;
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr){
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case MINUS:
                return (double) left - (double) right;
            case STAR:
                return (double) left * (double) right;
            case SLASH:
                return (double) left / (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String){
                    return (String) left + (String) right;
                }
                if (left instanceof String && right instanceof Double){
                    return (String) left + (Double) right;
                }
                if (left instanceof Double && right instanceof String){
                    return (Double) left + (String) right;
                }
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                return null;
        }
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTruthy(Object object){
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object left, Object right){
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private String stringify(Object object){
        if (object == null) return "nil";

        if (object instanceof Double){
            String text = object.toString();
            if (text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }
}
