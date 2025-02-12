package src.main.java.com.lang.benzene.Interpreter.BenzeneCallable;

import java.util.List;
import src.main.java.com.lang.benzene.Interpreter.Interpreter;

public interface BenzeneCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}