package src.main.java.com.lang.benzene.Parser;

import java.util.ArrayList;
import java.util.List;
import src.main.java.com.lang.benzene.TreeNodes.Stmt;

import static src.main.java.com.lang.benzene.Tokens.TokenType.*;
import src.main.java.com.lang.benzene.Tokens.Token;
import src.main.java.com.lang.benzene.Tokens.TokenType;
import src.main.java.com.lang.benzene.TreeNodes.Expr;
import src.main.java.com.lang.benzene.Benzene;
import src.main.java.com.lang.benzene.Errors.ParseError;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Stmt> parse(){
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()){
      statements.add(declaration());
    }

    return statements;
  }

  private Stmt declaration(){
    try {
      if (match(VAR)) return varDeclaration();
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt varDeclaration(){
    Token name = consume(IDENTIFIER, "Expect variable name.");

    consume(SEMICOLON, "Expected ':' before type.");

    Token type = consume(TYPE, "Expected type after semi-colon.");
    String variableType = type.lexeme;

    Expr initializer = null;
    if (match(EQUAL)){
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, variableType, initializer);
  }

  private Stmt statement(){
    if (match(PRINT)) return printStatement();

    return expressionStatement();
  }

  private Stmt printStatement(){
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt expressionStatement(){
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Expr expression(){
    return equality();
  }

  private Expr equality(){
    Expr expr = comparision();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
        Token operator = previous();
        Expr right = comparision();
        expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr comparision(){
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
        Token operator = previous();
        Expr right = term();
        expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr term(){
    Expr expr = factor();

    while (match(MINUS, PLUS)){
        Token operator = previous();
        Expr right = factor();
        expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr factor(){
    Expr expr = unary();

    while (match(STAR, SLASH)){
        Token operator = previous();
        Expr right = unary();
        expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr unary(){
    if (match(BANG, MINUS)){
        Token operator = previous();
        Expr right = unary();
        return new Expr.Unary(operator, right);
    }

    return primary();
  }

  private Expr primary() {
    if (match(FALSE)) return new Expr.Literal(previous());
    if (match(TRUE)) return new Expr.Literal(previous());
    if (match(NIL)) return new Expr.Literal(previous());

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous());
    }

    if (match(IDENTIFIER)) return new Expr.Variable(previous());

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private Token consume(TokenType token, String message){
    if (check(token)) return advance();
    throw error(peek(), message);
  }

  private ParseError error(Token token, String message){
    Benzene.error(token, message);
    return new ParseError();
  }

  private boolean match(TokenType... types){
    for (TokenType type : types){
        if (check(type)){
            advance();
            return true;
        }
    }

    return false;
  }

  private boolean check(TokenType type){
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance(){
    if (!isAtEnd()) current++;
    return previous();
  }
  
  private boolean isAtEnd(){
    return peek().type == EOF;
  }

  private Token peek(){
    return tokens.get(current);
  }

  private Token previous(){
    return tokens.get(current - 1);
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }
}