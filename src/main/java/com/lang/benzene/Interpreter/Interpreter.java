package src.main.java.com.lang.benzene.Interpreter;

import static src.main.java.com.lang.benzene.Tokens.TokenType.AND;
import static src.main.java.com.lang.benzene.Tokens.TokenType.MINUS;
import static src.main.java.com.lang.benzene.Tokens.TokenType.OR;
import static src.main.java.com.lang.benzene.Tokens.TokenType.PLUS;
import static src.main.java.com.lang.benzene.Tokens.TokenType.SLASH;
import static src.main.java.com.lang.benzene.Tokens.TokenType.STAR;

import java.util.List;
import java.util.ArrayList;

import src.main.java.com.lang.benzene.Environment.Environment;
import src.main.java.com.lang.benzene.Errors.BreakError;
import src.main.java.com.lang.benzene.Errors.ContinueError;
import src.main.java.com.lang.benzene.Errors.ReturnError;
import src.main.java.com.lang.benzene.Errors.RuntimeError;
import src.main.java.com.lang.benzene.Interpreter.BenzeneCallable.BenzeneCallable;
import src.main.java.com.lang.benzene.TreeNodes.Expr;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;
import src.main.java.com.lang.benzene.Interpreter.BenzeneClass;

public class Interpreter implements  Expr.Visitor<Object>, Stmt.Visitor<Void> {
    public final Environment globals = new Environment();
    private Environment environment = globals;

    public void interpret(List<Stmt> statements){
        for (Stmt stmt : statements){
            execute(stmt);
        }
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        Object value = null;
        if (stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt){
        Environment fieldsEnvironment = new Environment();
        executeBlock(stmt.variables, fieldsEnvironment);

        environment.define(stmt.name.lexeme, null);
        BenzeneClass klass = new BenzeneClass(stmt.name.lexeme, fieldsEnvironment);
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        BenzeneFunction function = new BenzeneFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        while (isTruthy(evaluate(stmt.condition))){
            try {
                execute(stmt.body);
            } catch (BreakError error){
                break;
            } catch (ContinueError error){
                continue;
            }
        }

        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt){
        throw new ContinueError(stmt.continueToken, "Continue statement encountered on line " + stmt.continueToken.line + ".");
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt){
        throw new BreakError(stmt.breakToken, "Break statement encountered on line " + stmt.breakToken.line + ".");
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt){
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt){
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        throw new ReturnError(evaluate(stmt.value));
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt){
        if (isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }

        return null;
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
    public Object visitAssignExpr(Expr.Assign expr){
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        return environment.get(expr.name);
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
    public Object visitCallExpr(Expr.Call expr){
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments){
            arguments.add(evaluate(argument));
        }

        BenzeneCallable function = (BenzeneCallable)callee;

        return function.call(this, arguments);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr){
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case OR:
                return isTruthy(left) || isTruthy(right);
            case AND:
                return isTruthy(left) && isTruthy(right);
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

    private void execute(Stmt stmt){
        stmt.accept(this);
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
