package src.main.java.com.lang.benzene.Interpreter;

import src.main.java.com.lang.benzene.TreeNodes.Expr;

public class Interpreter implements  Expr.Visitor<Object> {
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


}
