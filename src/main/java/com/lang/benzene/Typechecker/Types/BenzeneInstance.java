package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.HashMap;
import java.util.Map;

public class BenzeneInstance extends Type {
    private BenzeneClass klass;

    public BenzeneInstance(BenzeneClass klass){
        super(klass.name);
        this.klass = klass;
    }

    public String getName(){
        return this.klass.name;
    }
}
