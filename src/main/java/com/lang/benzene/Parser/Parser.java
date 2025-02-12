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
      if (match(FUN)) return function("function");
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt.Function function(String kind){
    Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
    consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");

    List<Token> parameters = new ArrayList<>();
    List<String> paramTypes = new ArrayList<>();

    if (!check(RIGHT_PAREN)){
      do {
        if (parameters.size() >= 255){
          error(peek(), "Cannot have more than 255 parameters.");
        }

        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
        consume(COLON, "Expect ':' before parameter type.");
        paramTypes.add(consume(TYPE, "Expect parameter type.").lexeme);
      } while (match(COMMA));
    }

    consume(RIGHT_PAREN, "Expect ')' after parameters.");

    consume(COLON, "Expect ':' before return type.");
    String returnType = consume(TYPE, "Expect return type.").lexeme;

    consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
    List<Stmt> body = block();

    return new Stmt.Function(name, parameters, paramTypes, body, returnType);
  }

  private Stmt varDeclaration(){
    Token name = consume(IDENTIFIER, "Expect variable name.");

    consume(COLON, "Expected ':' before type.");

    Token type = consume(TYPE, "Expected type after colon.");
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
    if (match(LEFT_BRACE)) return new Stmt.Block(block());
    if (match(IF)) return ifStatement();
    if (match(WHILE)) return whileStatement();
    if (match(CONTINUE)) return continueStmt();
    if (match(BREAK)) return breakStmt();
    if (match(RETURN)) return returnStatement();

    return expressionStatement();
  }

  private Stmt returnStatement(){
    Token keyword = previous();

    // if return statement has no value, return nil
    Expr value = new Expr.Literal(new Token(NIL, "nil", null, keyword.line));
    if (!check(SEMICOLON)){
      value = expression();
    }

    consume(SEMICOLON, "Expect ';' after return value.");
    return new Stmt.Return(keyword, value);
  }

  private Stmt continueStmt(){
    consume(SEMICOLON, "Expect ';' after continue statement.");
    return new Stmt.Continue(previous());
  }

  private Stmt breakStmt(){
    consume(SEMICOLON, "Expect ';' after break statement.");
    return new Stmt.Break(previous());
  }

  private Stmt whileStatement(){
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after while condition.");
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt ifStatement(){
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition.");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)){
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
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

  private List<Stmt> block(){
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()){
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Expr expression(){
    return assignment();
  }

  private Expr assignment(){
    Expr expr = or();

    if (match(EQUAL)){
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable){
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr or(){
    Expr expr = and();

    while (match(OR)){
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr and(){
    Expr expr = equality();

    while (match(AND)){
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
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

    return call();
  }

  private Expr call(){
    Expr expr = primary();

    while (true){
      if (match(LEFT_PAREN)){
        expr = finishCall(expr);
      } else {
        break;
      }
    }

    return expr;
  }

  private Expr finishCall(Expr callee){
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)){
      do {
        if (arguments.size() >= 255){
          error(peek(), "Cannot have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }

    Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

    return new Expr.Call(callee, paren, arguments);
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