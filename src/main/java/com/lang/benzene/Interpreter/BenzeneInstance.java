package src.main.java.com.lang.benzene.Interpreter;

import java.util.HashMap;
import java.util.Map;

public class BenzeneInstance {
    private BenzeneClass klass;

    BenzeneInstance(BenzeneClass klass){
        this.klass = klass;
    }


    @Override
    public String toString(){
        return klass.name;
    }
}
