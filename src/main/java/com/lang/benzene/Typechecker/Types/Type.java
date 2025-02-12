package src.main.java.com.lang.benzene.Typechecker.Types;

public class Type {
    String name;

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

    public static Type getTypeFromString(String name){
        /**
         * This function needs to be implemented
         * Will need to implement a map for this to map each type to its correct type representation
         */

        return null; // placeholder return value
    }

    static public Type number = new Type("number");
    static public Type string = new Type("string");
    static public Type nil = new Type("nil");
    static public Type bool = new Type("boolean");
}
