package com.seungyeon.jlox;

import com.seungyeon.jlox.Expr.Binary;
import com.seungyeon.jlox.Expr.Grouping;
import com.seungyeon.jlox.Expr.Literal;
import com.seungyeon.jlox.Expr.Unary;
import java.util.List;

import static com.seungyeon.jlox.TokenType.*;

public class Parser {
  private static class ParseError extends RuntimeException {}

  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  Expr parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }

  private interface ExprCaller {
    public Expr call();
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
    return equality();
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
    return binaryOperatorHelper(this::unray, LESS, GREATER_EQUAL, LESS_EQUAL);
  }

  // unray -> ("!" | "-") unary | primary;
  private Expr unray() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unray();
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
