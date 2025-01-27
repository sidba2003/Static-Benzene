package src.main.java.com.lang.benzene.Typechecker;

import src.main.java.com.lang.benzene.TreeNodes.Expr;
import src.main.java.com.lang.benzene.Tokens.TokenType;

import src.main.java.com.lang.benzene.Typechecker.Types.Type;

public class Typechecker implements Expr.Visitor<Object> {
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
                return Type.nil;    // this is unreachable, but adding it due to java's mandatory return type requirement
        }
    }
}
