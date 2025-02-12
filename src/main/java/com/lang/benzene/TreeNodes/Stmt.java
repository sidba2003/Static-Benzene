package src.main.java.com.lang.benzene.TreeNodes;

import java.util.List;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Tokens.TokenType;

public abstract class Stmt {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R>{
        R visitExpressionStmt(Expression stmt);
        R visitWhileStmt(While stmt);
        R visitContinueStmt(Continue stmt);
        R visitBreakStmt(Break stmt);
        R visitBlockStmt(Block stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }

    public static class Expression extends Stmt {
        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        public final Expr expression;
    }

    public static class While extends Stmt {
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        public final Expr condition;
        public final Stmt body;
    }

    public static class Continue extends Stmt {
        public Continue(Token continueToken) {
            this.continueToken = continueToken;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }

        public final Token continueToken;
    }

    public static class Break extends Stmt {
        public Break(Token breakToken) {
            this.breakToken = breakToken;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }

        public final Token breakToken;
    }

    public static class Block extends Stmt {
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        public final List<Stmt> statements;
    }

    public static class Function extends Stmt {
        public Function(Token name, List<Token> params, List<String> paramTypes, List<Stmt> body, String returnType) {
            this.name = name;
            this.params = params;
            this.paramTypes = paramTypes;
            this.body = body;
            this.returnType = returnType;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        public final Token name;
        public final List<Token> params;
        public final List<String> paramTypes;
        public final List<Stmt> body;
        public final String returnType;
    }

    public static class If extends Stmt {
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;
    }

    public static class Print extends Stmt {
        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        public final Expr expression;
    }

    public static class Var extends Stmt {
        public Var(Token name, String type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        public final Token name;
        public final String type;
        public final Expr initializer;
    }

}
