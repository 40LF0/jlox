package com.seungyeon.jlox;

import static com.seungyeon.jlox.TokenType.*;

import com.seungyeon.jlox.Expr.Assign;
import com.seungyeon.jlox.Expr.Binary;
import com.seungyeon.jlox.Expr.Grouping;
import com.seungyeon.jlox.Expr.Literal;
import com.seungyeon.jlox.Expr.Unary;
import com.seungyeon.jlox.Expr.Variable;
import java.util.ArrayList;
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
    Expr expr = equality();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if(expr instanceof Variable) {
        Token name = ((Variable) expr).name;
        return new Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Stmt statement() {
    if (match(PRINT)) return printStatement();

    return expressionStatement();
  }

  private Stmt declaration() {
    try {
      if (match(VAR)) return varDeclaration();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
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

  private Stmt expressionStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Expression(value);
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

  // unray -> ("!" | "-") unary | primary;
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Unary(operator, right);
    }
    return primary();
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

    while (!isAtEnd()){
      if (previous().type == SEMICOLON) return;

      switch (peek().type){
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
