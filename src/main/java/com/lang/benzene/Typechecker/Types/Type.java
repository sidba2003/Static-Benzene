package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.TypeNotFoundError;

public class Type {
    String name;

    private static Map<String, Type> typeMap = new HashMap<String, Type>();

    public Type(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public boolean equals(String otherType){
        return this.getName().equals(otherType.substring(2, otherType.length() - 2));
    }

    public boolean equals(Type otherType){
        return this.getName().equals(otherType.getName());
    }

    public static void updateTypeMap(String type, Type typeValue){
        typeMap.put("<<" + type + ">>", typeValue);
    }

    public static Type getTypeFromString(String name){
        if (typeMap.containsKey(name)){
            return typeMap.get(name);
        }

        // will need to implement different logic for class instance retireval
        // for ex, if if we have class a{}...its type will be <<cls<a>>> and its instance type will be <<a>>

        throw new TypeNotFoundError("Type " + name + " not found.");
    }

    static public Type number = new Type("number");
    static public Type string = new Type("string");
    static public Type nil = new Type("nil");
    static public Type bool = new Type("boolean");

    static {
        typeMap.put("<<number>>", Type.number);
        typeMap.put("<<string>>", Type.string);
        typeMap.put("<<nil>>", Type.nil);
        typeMap.put("<<boolean>>", Type.bool);
    }
}
