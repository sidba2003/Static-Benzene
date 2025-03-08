package src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable;

import java.util.ArrayList;

import src.main.java.com.lang.benzene.Interpreter.Interpreter;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.Type;

public interface BenzeneCallable {
    Object call(Typechecker typechecker);
    void checkCall(ArrayList<Type> aurgumentTypes);
    int arity();
    String getReturnTypeString();
}
