package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinaryExpr implements Expression {

    private final Expression left;
    private final String operator;
    private final Expression right;

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                '}';
    }

}
