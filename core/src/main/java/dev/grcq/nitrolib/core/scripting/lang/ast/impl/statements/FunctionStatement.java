package dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements;

import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class FunctionStatement implements Statement {

    private final String name;
    private final String[] parameters;
    private final Block body;

    @Override
    public String toString() {
        return "FunctionStatement{" +
                "name='" + name + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", body=" + body +
                '}';
    }

}
