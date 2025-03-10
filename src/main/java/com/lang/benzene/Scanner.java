package src.main.java.com.lang.benzene;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static src.main.java.com.lang.benzene.Tokens.TokenType.*;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Tokens.TokenType;


public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
        keywords.put("continue", CONTINUE);
        keywords.put("break", BREAK);
    }

    Scanner(String source){
        this.source = source;
    }

    public List<Token> scanTokens(){
        while (!isAtEnd()){
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken(){
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case ':': addToken(COLON); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                if (match('<')) {
                    parseTypeValue();
                    addToken(TYPE);
                } else {
                    addToken(match('=') ? LESS_EQUAL : LESS);
                }
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')){
                    parseMultiLineComments();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
        
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)){
                    identifier();
                } else {
                    Benzene.error(line, "Unexpected character.");
                }
        }
    }

    private void parseMultiLineComments(){
        while (!isAtEnd()){
            if (peek() == '*'){
                advance();
                if (peek() == '/') {
                    advance();
                    return;
                }
                continue;
            }

            if (peek() == '\n') line += 1;

            advance();
        }

        Benzene.error(line, "Did not encounter end of line while parsing multi-line comment.");
    }

    private void identifier(){
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlpha(char c){
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isDigit(c) || isAlpha(c);
    }

    private boolean isDigit(char c){
        return '0' <= c && c <= '9';
    }

    private void number(){
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())){
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext(){
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void string(){
        while (peek() != '"' && !isAtEnd()){
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()){
            Benzene.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek(){
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void parseTypeValue(){
        int openBraces = 2;

        while (openBraces > 0 && !isAtEnd()){
            if (peek() == '<') openBraces += 1;
            else if (peek() == '>') openBraces -= 1;

            advance();
        }

        if (openBraces > 0 && isAtEnd()) Benzene.error(line, "Type opening never closed.");
    }

    private boolean match(char expected){
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char advance(){
        return source.charAt(current++);
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }
}
