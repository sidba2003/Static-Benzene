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
        return this.name.equals(otherType);
    }

    public Type getTypeFromString(String name){
        /**
         * This function needs to be implemented
         * Will need to implement a map for this to map each string to its correct type
         */

        return this; // placeholder return value
    }

    static public Type number = new Type("number");
    static public Type string = new Type("string");
    static public Type nil = new Type("nil");
    static public Type bool = new Type("boolean");
}
