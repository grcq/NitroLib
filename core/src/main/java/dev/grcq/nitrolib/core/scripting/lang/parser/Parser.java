package dev.grcq.nitrolib.core.scripting.lang.parser;

import dev.grcq.nitrolib.core.scripting.lang.ast.ASTNode;
import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.*;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.FunctionStatement;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.Program;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.VariableDeclaration;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token expect(TokenType type) {
        if (at().getType() == type) {
            return next();
        } else {
            throw new IllegalStateException("Expected token of type $type but found " + at().getType());
        }
    }

    private boolean is(TokenType type) {
        return is(type, true);
    }

    private boolean is(TokenType type, boolean skip) {
        boolean result = at().getType() == type;
        if (result && skip) next();

        return result;
    }

    private Token next() {
        return tokens.get(current++);
    }

    private Token at() {
        return tokens.get(current);
    }

    private Token at(int offset) {
        return tokens.get(current + offset);
    }

    private boolean isEof() {
        return this.at().getType() == TokenType.EOF;
    }

    public Program parse() {
        List<ASTNode> body = new ArrayList<>();

        while (!isEof()) {
            ASTNode statement = parseStatement();
            body.add(statement);
        }

        return new Program(body.toArray(new ASTNode[0]));
    }

    private Statement parseStatement() {
        switch (at().getType()) {
            case FUNCTION:
                return parseFunctionStatement();
            case VARIABLE:
                return parseVariableStatement();
            default:
                return parseExpression();
        }
    }

    private Statement.Block parseBlockStatement() {
        expect(TokenType.OPEN_BRACE);
        List<Statement> statements = new ArrayList<>();
        while (at().getType() != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement());
        }
        expect(TokenType.CLOSE_BRACE);
        return new Statement.Block(statements.toArray(new Statement[0]));
    }

    private Statement parseFunctionStatement() {
        expect(TokenType.FUNCTION);
        Token name = expect(TokenType.IDENTIFIER);
        expect(TokenType.OPEN_PAREN);
        List<String> parameters = new ArrayList<>();
        while (at().getType() != TokenType.CLOSE_PAREN) {
            Token param = expect(TokenType.IDENTIFIER);
            parameters.add(param.getToken());
        }
        expect(TokenType.CLOSE_PAREN);
        return new FunctionStatement(
            name.getToken(),
            parameters.toArray(new String[0]),
            parseBlockStatement()
        );
    }

    private Statement parseVariableStatement() {
        expect(TokenType.VARIABLE);
        boolean constant = is(TokenType.CONSTANT);
        Token name = expect(TokenType.IDENTIFIER);
        Expression value = new NullLiteral();
        LogUtil.info("Parsing variable: " + name.getToken());
        LogUtil.info("Current token: " + at().getType());
        LogUtil.info("Next token: " + at(1).getType());
        if (is(TokenType.EQUALS)) {
            LogUtil.info("equals found, at: " + at().getType());
            value = parseExpression();
            LogUtil.info("Parsed value: " + value);
        }

        return new VariableDeclaration(name.getToken(), constant, value);
    }

    private Expression parseExpression() {
        return parseAdditive();
    }

    private Expression parseAdditive() {
        Expression left = parseMultiplicative();
        while (is(TokenType.PLUS, false) || is(TokenType.MINUS, false)) {
            Token operator = next();
            Expression right = parseMultiplicative();
            left = new BinaryExpr(left, operator.getToken(), right);
        }

        return left;
    }

    private Expression parseMultiplicative() {
        Expression left = parseFunctionCall();
        while (is(TokenType.ASTERISK, false) || is(TokenType.SLASH, false)) {
            Token operator = next();
            Expression right = parsePrimary();
            left = new BinaryExpr(left, operator.getToken(), right);
        }
        return left;
    }

    private Expression parseFunctionCall() {
        Expression left = parsePrimary();
        while (at().getType() == TokenType.OPEN_PAREN) {
            expect(TokenType.OPEN_PAREN);
            List<Expression> arguments = new ArrayList<>();
            while (at().getType() != TokenType.CLOSE_PAREN) {
                arguments.add(parseExpression());
                if (at().getType() == TokenType.COMMA) {
                    expect(TokenType.COMMA);
                }
            }
            expect(TokenType.CLOSE_PAREN);
            left = new FunctionCall(left, arguments.toArray(new Expression[0]));
        }

        return left;
    }

    private Expression parsePrimary() {
        switch (at().getType()) {
            case NUMBER: {
                Token number = expect(TokenType.NUMBER);
                return new NumberLiteral(Double.parseDouble(number.getToken()));
            }
            case STRING: {
                Token string = expect(TokenType.STRING);
                return new StringLiteral(string.getToken());
            }
            case BOOLEAN: {
                Token bool = expect(TokenType.BOOLEAN);
                return new BooleanLiteral(Boolean.parseBoolean(bool.getToken()));
            }
            case NULL: {
                expect(TokenType.NULL);
                return new NullLiteral();
            }
            case IDENTIFIER: {
                Token identifier = expect(TokenType.IDENTIFIER);
                if (is(TokenType.EQUALS)) {
                    Expression value = parseExpression();
                    return new AssignmentExpr(identifier.getToken(), value);
                }
                return new IdentifierLiteral(identifier.getToken());
            }
            default:
                throw new IllegalStateException("Unexpected token: " + at().getType());
        }
    }

}