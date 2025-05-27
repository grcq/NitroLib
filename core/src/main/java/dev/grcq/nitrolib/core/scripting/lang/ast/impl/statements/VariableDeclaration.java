package dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements;

import dev.grcq.nitrolib.core.scripting.lang.ast.Expression;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class VariableDeclaration implements Statement {

    @NotNull
    private final String name;
    private boolean constant;
    @NotNull
    private final Expression value;

}
