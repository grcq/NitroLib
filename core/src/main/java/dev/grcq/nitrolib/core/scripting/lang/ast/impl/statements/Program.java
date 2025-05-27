package dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements;

import dev.grcq.nitrolib.core.scripting.lang.ast.ASTNode;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Program implements Statement {
    private final ASTNode[] body;

    @Override
    public String toString() {
        return "Program{" +
                "body=" + body +
                '}';
    }
}
