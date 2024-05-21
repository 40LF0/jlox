package com.seungyeon.jlox;

import static com.seungyeon.jlox.TokenType.*;

import com.seungyeon.jlox.Expr.Assign;
import com.seungyeon.jlox.Expr.Binary;
import com.seungyeon.jlox.Expr.Grouping;
import com.seungyeon.jlox.Expr.Literal;
import com.seungyeon.jlox.Expr.Unary;
import com.seungyeon.jlox.Expr.Variable;
import com.seungyeon.jlox.Stmt.Block;
import com.seungyeon.jlox.Stmt.While;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Parser {
  private static class ParseError extends RuntimeException {}

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  }

  private interface ExprCaller {
    Expr call();
  }

  private Expr binaryOperatorHelper(ExprCaller caller, TokenType... tokens) {
    Expr expr = caller.call();
    while (match(tokens)) {
      Token operator = previous();
      Expr right = caller.call();
      expr = new Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Variable) {
        Token name = ((Variable) expr).name;
        return new Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Stmt statement() {
    if (match(FOR)) return forStatement();
    if (match(IF)) return ifStatement();
    if (match(PRINT)) return printStatement();
    if (match(RETURN)) return returnStatement();
    if (match(WHILE)) return whileStatement();
    if (match(LEFT_BRACE)) return new Block(block());
    return expressionStatement();
  }

  private Stmt forStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'for'.");

    Stmt initializer;
    if (match(SEMICOLON)) {
      initializer = null;
    } else if (match(VAR)) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check(SEMICOLON)) {
      condition = expression();
    }
    consume(SEMICOLON, "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check(RIGHT_PAREN)) {
      increment = expression();
    }
    consume(RIGHT_PAREN, "Expect ')' after for clauses.");
    Stmt body = statement();

    if (increment != null) {
      body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
    }

    if (condition == null) condition = new Expr.Literal(true);
    body = new While(condition, body);

    if (initializer != null) body = new Stmt.Block(Arrays.asList(initializer, body));

    return body;
  }

  private Stmt ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt declaration() {
    try {
      if (match(CLASS)) return classDeclaration();
      if (match(FUN)) return function("function");
      if (match(VAR)) return varDeclaration();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt classDeclaration() {
    Token name = consume(IDENTIFIER, "Expect class name.");
    consume(LEFT_BRACE, "Expect '{' before class body.");

    List<Stmt.Function> methods = new ArrayList<>();
    while (!check(RIGHT_BRACE) && !isAtEnd()){
      methods.add(function("method"));
    }

    consume(RIGHT_BRACE, "Expect '}' after class body.");

    return new Stmt.Class(name, methods);
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    if (!check(SEMICOLON)){
      value = expression();
    }

    consume(SEMICOLON, "Expect ';' after return value.");
    return new Stmt.Return(keyword, value);
  }

  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

  private Stmt whileStatement() {
    consume(LEFT_PAREN, "Except '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition.");
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt expressionStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Expression(value);
  }

  private Stmt.Function function(String kind) {
    Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
    consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<Token> parameters = new ArrayList<>();
    if (!check(RIGHT_PAREN)){
      do {
        if (parameters.size() >= 255){
          error(peek(), "Can't have more than 255 parameters.");
        }

        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
      } while (match(COMMA));
    }
    consume(RIGHT_PAREN, "Expect ')' after parameters.");

    consume(LEFT_BRACE, "Expect '{' before" + kind + " body.");
    List<Stmt> body = block();
    return new Stmt.Function(name, parameters, body);
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) statements.add(declaration());

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  // equality -> comparison (("!=" | "==") comparison)* ;
  private Expr equality() {
    return binaryOperatorHelper(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
  }

  // comparison -> term((">" | ">=" | "<" | "<=") term)*;
  private Expr comparison() {
    return binaryOperatorHelper(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
  }

  // term -> factor(("-" | "+") factor)*;
  private Expr term() {
    return binaryOperatorHelper(this::factor, MINUS, PLUS);
  }

  // factor -> unray(("/" | "*") unary)*;
  private Expr factor() {
    return binaryOperatorHelper(this::unary, SLASH, STAR);
  }

  // unray -> ("!" | "-") unary | call;
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Unary(operator, right);
    }
    return call();
  }

  // call -> primary ("(" arguments? ")")*;
  private Expr call() {
    Expr expr = primary();

    while (true) {
      if (match(LEFT_PAREN)) {
        expr = finishCall(expr);
      } else {
        break;
      }
    }

    return expr;
  }

  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }
    Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

    return new Expr.Call(callee, paren, arguments);
  }

  // primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
  private Expr primary() {
    if (match(FALSE)) return new Literal(false);
    if (match(TRUE)) return new Literal(true);
    if (match(NIL)) return new Literal(null);

    if (match(NUMBER, STRING)) {
      return new Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression");
      return new Grouping(expr);
    }
    throw error(peek(), "Expect expression.");
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
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

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
