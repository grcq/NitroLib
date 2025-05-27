package dev.grcq.nitrolib.core.scripting.lang.eval;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.ASTNode;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

public interface IEvaluator<T extends ASTNode> {

    RuntimeValue evaluate(T node, Environment env);

}
