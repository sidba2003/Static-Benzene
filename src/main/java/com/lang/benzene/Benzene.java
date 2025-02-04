package src.main.java.com.lang.benzene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Tokens.TokenType;
import src.main.java.com.lang.benzene.Errors.TypeMismatchError;
import src.main.java.com.lang.benzene.Errors.ValueNotFoundError;
import src.main.java.com.lang.benzene.Parser.Parser;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;
import src.main.java.com.lang.benzene.Typechecker.Typechecker;
import src.main.java.com.lang.benzene.Interpreter.Interpreter;


public class Benzene {
    static boolean hadError = false;
    static boolean hadTypecheckError = false;

    private static final Typechecker typeChecker = new Typechecker();
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException{
        if (args.length > 1){
            System.out.println("Usage: benzene [script]");
            System.exit(64);
        } else if(args.length == 1){
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
        if (hadTypecheckError) System.exit(75);
    }

    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;){
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        typeChecker.typecheck(statements);

        // we dont interpret input if a typechecking error has been found
        if (!hadTypecheckError) interpreter.interpret(statements);
        hadTypecheckError = false;
    }

    static void error(int line, String message){
        report(line, "", message);
    }

    public static void report(int line, String where, String message){
        System.err.println("[line" + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void error(Token token, String message){
        if (token.type == TokenType.EOF) {
            report(token.line, "at end", message);
        } else {
            report(token.line, " at " + token.lexeme + "", message);
        }
    }

    public static void typecheckError(TypeMismatchError error){
        System.err.println(error.getMessage() + "\n[Line " + error.token.line + "]");
        hadTypecheckError = true;
    }

    public static void typecheckError(ValueNotFoundError error){
        System.err.println(error.getMessage() + "\n[Line " + error.token.line + "]");
        hadTypecheckError = true;
    }
}