package com.seungyeon.jlox;

class Token {
  final TokenType type;
  // lexeme is the smallest unit of meaningful sequence of the sentence
  final String lexeme;
  final Object literal;
  final int line;

  public Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
