package com.seungyeon.jlox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ScannerTest {
  @Test
  public void testSingleCharacterTokens() {
    String source = "(){},.-+;*";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(11, tokens.size());

    assertEquals(TokenType.LEFT_PAREN, tokens.get(0).type);
    assertEquals(TokenType.RIGHT_PAREN, tokens.get(1).type);
    assertEquals(TokenType.LEFT_BRACE, tokens.get(2).type);
    assertEquals(TokenType.RIGHT_BRACE, tokens.get(3).type);
    assertEquals(TokenType.COMMA, tokens.get(4).type);
    assertEquals(TokenType.DOT, tokens.get(5).type);
    assertEquals(TokenType.MINUS, tokens.get(6).type);
    assertEquals(TokenType.PLUS, tokens.get(7).type);
    assertEquals(TokenType.SEMICOLON, tokens.get(8).type);
    assertEquals(TokenType.STAR, tokens.get(9).type);
    assertEquals(TokenType.EOF, tokens.get(10).type);
  }

  @Test
  public void testOperatorTokensWithNextCharacter() {
    String source = "! != = == < <= > >=";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(9, tokens.size());

    assertEquals(TokenType.BANG, tokens.get(0).type);
    assertEquals(TokenType.BANG_EQUAL, tokens.get(1).type);
    assertEquals(TokenType.EQUAL, tokens.get(2).type);
    assertEquals(TokenType.EQUAL_EQUAL, tokens.get(3).type);
    assertEquals(TokenType.LESS, tokens.get(4).type);
    assertEquals(TokenType.LESS_EQUAL, tokens.get(5).type);
    assertEquals(TokenType.GREATER, tokens.get(6).type);
    assertEquals(TokenType.GREATER_EQUAL, tokens.get(7).type);
    assertEquals(TokenType.EOF, tokens.get(8).type);
  }

  @Test
  public void testSlashOperatorVsComment() {
    String source = "/ / // this is a comment";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(3, tokens.size());

    assertEquals(TokenType.SLASH, tokens.get(0).type);
    assertEquals(TokenType.SLASH, tokens.get(1).type);
    assertEquals(TokenType.EOF, tokens.get(2).type);
  }

  @Test
  public void testWhitespaceAndNewlineCharacters() {
    String source = " \r\t\n";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(1, tokens.size());

    assertEquals(TokenType.EOF, tokens.get(0).type);
  }

  @Test
  public void testStringLiteral() {
    String source = "\"Hello, world!\"";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(2, tokens.size());

    assertEquals(TokenType.STRING, tokens.get(0).type);
    assertEquals("Hello, world!", tokens.get(0).literal);
    assertEquals(TokenType.EOF, tokens.get(1).type);
  }

  @Test
  public void testNumericLiteral() {
    String source = "123 45.67";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(3, tokens.size());

    assertEquals(TokenType.NUMBER, tokens.get(0).type);
    assertEquals(123.0, tokens.get(0).literal);
    assertEquals(TokenType.NUMBER, tokens.get(1).type);
    assertEquals(45.67, tokens.get(1).literal);
    assertEquals(TokenType.EOF, tokens.get(2).type);
  }

  @Test
  public void testIdentifier() {
    String source = "if else var x variable";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(6, tokens.size());

    assertEquals(TokenType.IF, tokens.get(0).type);
    assertEquals(TokenType.ELSE, tokens.get(1).type);
    assertEquals(TokenType.VAR, tokens.get(2).type);
    assertEquals(TokenType.IDENTIFIER, tokens.get(3).type);
    assertEquals(TokenType.IDENTIFIER, tokens.get(4).type);
    assertEquals(TokenType.EOF, tokens.get(5).type);
  }

  @Test
  public void testReservedKeywords() {
    String source =
        "if else var and class else false for fun if nil or print return super this true var while";
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    assertEquals(20, tokens.size());

    assertEquals(TokenType.IF, tokens.get(0).type);
    assertEquals(TokenType.ELSE, tokens.get(1).type);
    assertEquals(TokenType.VAR, tokens.get(2).type);
    assertEquals(TokenType.AND, tokens.get(3).type);
    assertEquals(TokenType.CLASS, tokens.get(4).type);
    assertEquals(TokenType.ELSE, tokens.get(5).type);
    assertEquals(TokenType.FALSE, tokens.get(6).type);
    assertEquals(TokenType.FOR, tokens.get(7).type);
    assertEquals(TokenType.FUN, tokens.get(8).type);
    assertEquals(TokenType.IF, tokens.get(9).type);
    assertEquals(TokenType.NIL, tokens.get(10).type);
    assertEquals(TokenType.OR, tokens.get(11).type);
    assertEquals(TokenType.PRINT, tokens.get(12).type);
    assertEquals(TokenType.RETURN, tokens.get(13).type);
    assertEquals(TokenType.SUPER, tokens.get(14).type);
    assertEquals(TokenType.THIS, tokens.get(15).type);
    assertEquals(TokenType.TRUE, tokens.get(16).type);
    assertEquals(TokenType.VAR, tokens.get(17).type);
    assertEquals(TokenType.WHILE, tokens.get(18).type);
    assertEquals(TokenType.EOF, tokens.get(19).type);
  }
}
