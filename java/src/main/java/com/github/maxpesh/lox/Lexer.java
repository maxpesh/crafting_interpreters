package com.github.maxpesh.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start; // first character in the lexeme being scanned
    private int current; // character currently being considered
    private int line = 1; // tracks what source line `current` is on
    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("and", TokenType.AND),
            Map.entry("class", TokenType.CLASS),
            Map.entry("else", TokenType.ELSE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("for", TokenType.FOR),
            Map.entry("fun", TokenType.FUN),
            Map.entry("if", TokenType.IF),
            Map.entry("nil", TokenType.NIL),
            Map.entry("or", TokenType.OR),
            Map.entry("print", TokenType.PRINT),
            Map.entry("return", TokenType.RETURN),
            Map.entry("super", TokenType.SUPER),
            Map.entry("this", TokenType.THIS),
            Map.entry("true", TokenType.TRUE),
            Map.entry("var", TokenType.VAR),
            Map.entry("while", TokenType.WHILE)
    );

    Lexer(String source) {
        this.source = source;
    }

    List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);
            case '{' -> addToken(TokenType.LEFT_BRACE);
            case '}' -> addToken(TokenType.RIGHT_BRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case ';' -> addToken(TokenType.SEMICOLON);
            case '*' -> addToken(TokenType.STAR);
            case '!' -> addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '=' -> addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '<' -> addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '>' -> addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '"' -> string();
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
            }
            case ' ', '\r', '\t' -> {
            }
            case '\n' -> line++;
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') { // Lox supports multi-line strings
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }
        advance(); // The closing "
        var strLit = source.substring(start + 1, current - 1); // Trim the surrounding quotes
        addToken(TokenType.STRING, strLit);
    }

    private void number() {
        // integer part
        while (isDigit(peek())) {
            advance();
        }
        // fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }
        double num = Double.parseDouble(source.substring(start, current));
        addToken(TokenType.NUMBER, num);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        var text = source.substring(start, current);
        var tokenType = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(tokenType);
    }

    private void blockComment() {
        while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            } else if (peek() == '/' && peekNext() == '*') {
                // nested block comment
                advance(); // skip /
                advance(); // skip *
                blockComment();
                continue;
            }
            advance();
        }
        advance(); // skip *
        advance(); // skip /
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        var lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}