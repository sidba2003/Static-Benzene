package src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable;

import src.main.java.com.lang.benzene.Interpreter.Interpreter;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;

public interface BenzeneCallable {
    Object call(Typechecker typechecker);
    int arity();
}
