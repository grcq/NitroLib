package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class FunctionCall implements Expression {

    private final Expression callee;
    private final Expression[] arguments;

    @Override
    public String toString() {
        return "FunctionCall{" +
                "callee='" + callee + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
