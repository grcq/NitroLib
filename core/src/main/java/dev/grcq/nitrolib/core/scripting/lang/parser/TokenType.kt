package dev.grcq.nitrolib.core.scripting.lang.parser

enum class TokenType {
    // Single-character tokens
    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACE,
    CLOSE_BRACE,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    SEMICOLON,
    COMMA,
    DOT,
    COLON,
    EQUALS,
    GREATER,
    LESS,
    EXCLAMATION,
    AMPERSAND,
    PIPE,
    QUESTION,

    // Multi-character tokens
    NUMBER,
    STRING,
    BOOLEAN,
    NULL,

    // Keywords
    IF,
    ELSE,
    WHILE,
    FOR,
    VARIABLE,
    CONSTANT,
    FUNCTION,
    RETURN,
    BREAK,
    CONTINUE,
    LISTEN,

    IDENTIFIER,
    EOF,

    ERROR
}