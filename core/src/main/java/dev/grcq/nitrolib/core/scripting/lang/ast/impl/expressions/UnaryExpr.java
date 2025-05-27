package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnaryExpr implements Expression {

    private final String operator;
    private final Expression expression;

    @Override
    public String toString() {
        return "UnaryExpression{" +
                "operator='" + operator + '\'' +
                ", expression=" + expression +
                '}';
    }

}
