package src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable;

import java.util.ArrayList;
import java.util.List;

import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Typechecker.Types.Type;

public interface BenzeneCallable {
    Object typecheck(Typechecker typechecker);
    Object call(Typechecker typechecker, List<Type> arguments);
    void checkCall(ArrayList<Type> aurgumentTypes);
    int arity();
    Type getReturnTypeString();
}
