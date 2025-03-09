package src.main.java.com.lang.benzene.Typechecker.Types;

import java.util.HashMap;
import java.util.Map;

import src.main.java.com.lang.benzene.Errors.TypeNotFoundError;
import src.main.java.com.lang.benzene.Typechecker.Types.BenzeneCallable.BenzeneCallable;

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

    @Override
    public String toString(){
        return this.name;
    }

    public static void updateTypeMap(String type, Type typeValue){
        typeMap.put("<<" + type + ">>", typeValue);
    }

    public static Type getTypeFromString(String name){
        if (typeMap.containsKey(name)){
            return typeMap.get(name);
        }
        
        // considering the case where we want a class instance
        String className = name.substring(2, name.length() - 2);
        String classType = "<<cls<" + className + ">>>";

        if (typeMap.containsKey(classType)){
            BenzeneCallable callable = (BenzeneCallable) typeMap.get(classType);
            return (Type) callable.call(null, null);
        }
        
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
