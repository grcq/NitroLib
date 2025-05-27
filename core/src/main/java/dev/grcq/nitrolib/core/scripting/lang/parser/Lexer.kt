package dev.grcq.nitrolib.core.scripting.lang.parser

import dev.grcq.nitrolib.core.utils.LogUtil

class Lexer(private val input: String) {

    private var index: Int = 0
    private var current: Char = input[index]

    private val keywords: Map<String, TokenType> = mapOf(
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "while" to TokenType.WHILE,
        "for" to TokenType.FOR,
        "var" to TokenType.VARIABLE,
        "const" to TokenType.CONSTANT,
        "function" to TokenType.FUNCTION,
        "return" to TokenType.RETURN,
        "break" to TokenType.BREAK,
        "continue" to TokenType.CONTINUE,
        "listen" to TokenType.LISTEN,
        "true" to TokenType.BOOLEAN,
        "false" to TokenType.BOOLEAN,
        "null" to TokenType.NULL
    )

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (index <= input.length) {
            LogUtil.info("current: $current, index: $index, size: ${input.length}")
            if (current == '\u0000') break

            when (current) {
                '+' -> tokens.add(Token(TokenType.PLUS, advance()))
                '-' -> tokens.add(Token(TokenType.MINUS, advance()))
                '*' -> tokens.add(Token(TokenType.ASTERISK, advance()))
                '/' -> {
                    val prev = advance()
                    if (current == '/') {
                        while (current != '\n' && index < input.length) {
                            advance()
                        }
                    } else {
                        tokens.add(Token(TokenType.SLASH, prev))
                    }
                }
                '(' -> tokens.add(Token(TokenType.OPEN_PAREN, advance()))
                ')' -> tokens.add(Token(TokenType.CLOSE_PAREN, advance()))
                '{' -> tokens.add(Token(TokenType.OPEN_BRACE, advance()))
                '}' -> tokens.add(Token(TokenType.CLOSE_BRACE, advance()))
                '[' -> tokens.add(Token(TokenType.OPEN_BRACKET, advance()))
                ']' -> tokens.add(Token(TokenType.CLOSE_BRACKET, advance()))
                ';' -> tokens.add(Token(TokenType.SEMICOLON, advance()))
                ',' -> tokens.add(Token(TokenType.COMMA, advance()))
                '.' -> tokens.add(Token(TokenType.DOT, advance()))
                ':' -> tokens.add(Token(TokenType.COLON, advance()))
                '=' -> tokens.add(Token(TokenType.EQUALS, advance()))
                '>' -> tokens.add(Token(TokenType.GREATER, advance()))
                '<' -> tokens.add(Token(TokenType.LESS, advance()))
                '!' -> tokens.add(Token(TokenType.EXCLAMATION, advance()))
                '&' -> tokens.add(Token(TokenType.AMPERSAND, advance()))
                '|' -> tokens.add(Token(TokenType.PIPE, advance()))
                '?' -> tokens.add(Token(TokenType.QUESTION, advance()))
                '"', '\'' -> {
                    val stringBuilder = StringBuilder()
                    val prev = advance()
                    while (current != prev[0] && index < input.length) {
                        stringBuilder.append(current)
                        advance()
                    }
                    if (current == prev[0]) {
                        advance() // Skip the closing quote
                        tokens.add(Token(TokenType.STRING, stringBuilder.toString()))
                    } else {
                        tokens.add(Token(TokenType.ERROR, "Unterminated string literal"))
                    }
                }
                else -> {
                    if (current.isWhitespace()) {
                        advance()
                        continue
                    }

                    if (current.isDigit()) {
                        val numberBuilder = StringBuilder()
                        var isDecimal = false
                        while (current.isDigit() || current == '.') {
                            if (current == '.') {
                                if (isDecimal) {
                                    tokens.add(Token(TokenType.ERROR, "Invalid number format"))
                                    break
                                }

                                isDecimal = true
                            }

                            numberBuilder.append(current)
                            advance()
                        }

                        LogUtil.info("numberBuilder: $numberBuilder")
                        tokens.add(Token(TokenType.NUMBER, numberBuilder.toString()))
                        continue
                    }

                    if (current.isLetter()) {
                        val identifier = StringBuilder()
                        while (current.isLetterOrDigit()) {
                            identifier.append(current)
                            advance()
                        }

                        val keywordType = keywords[identifier.toString()] ?: TokenType.IDENTIFIER
                        tokens.add(Token(keywordType, identifier.toString()))
                        continue
                    }

                    // Handle other token types
                    tokens.add(Token(TokenType.ERROR, "Unexpected character: $current"))
                    advance()
                }
            }
        }

        tokens.add(Token(TokenType.EOF, "eof"))
        return tokens
    }

    private fun advance(): String {
        val prev = current
        index++
        current = if (index < input.length) input[index] else '\u0000'
        return prev.toString()
    }

}