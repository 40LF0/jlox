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
}
