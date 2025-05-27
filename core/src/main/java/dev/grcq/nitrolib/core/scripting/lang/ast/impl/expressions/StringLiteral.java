package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringLiteral implements Expression {

    private final String value;

    @Override
    public String toString() {
        return "StringLiteral{" +
                "value='" + value + '\'' +
                '}';
    }

}
