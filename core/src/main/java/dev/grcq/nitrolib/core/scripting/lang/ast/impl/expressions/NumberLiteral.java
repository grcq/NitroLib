package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NumberLiteral implements Expression {

    private final double value;

    @Override
    public String toString() {
        return "NumberLiteral{" +
                "value=" + value +
                '}';
    }

}
