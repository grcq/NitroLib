package dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignmentExpr implements Expression {

    private final String identifier;
    private final Expression value;

    @Override
    public String toString() {
        return "AssignmentExpr{" +
                "identifier='" + identifier + '\'' +
                ", value=" + value +
                '}';
    }

}
