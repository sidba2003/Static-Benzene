package src.main.java.com.lang.Tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
          System.err.println("Usage: generate_ast <output directory>");
          System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Token literalToken",
            "Unary : Token operator, Expr right",
            "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
            "Expression : Expr expression",
            "Print : Expr expression",
            "Var : Token name, String type, Expr initializer"
        ));
    }

    private static void defineAst(String outPutDir, String baseName, List<String> types) throws IOException {
        String path = outPutDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package src.main.java.com.lang.benzene.TreeNodes;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println("import src.main.java.com.lang.benzene.Tokens.Token;");
        writer.println("import src.main.java.com.lang.benzene.Tokens.TokenType;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");

        writer.println();
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        defineVisitor(writer, baseName, types);
        writer.println();

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
            writer.println();
        }

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types){
        writer.println("    public interface Visitor<R>{");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
          }
      
          writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList){
        writer.println("    public static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("        public " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
        String name = field.split(" ")[1];
        writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");


        // Fields.  
        writer.println();
        for (String field : fields) {
        writer.println("        public final " + field + ";");
        }

        writer.println("    }");
    }
}
