package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.IdentifierLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

public class IdentifierEval implements IEvaluator<IdentifierLiteral> {

    @Override
    public RuntimeValue evaluate(IdentifierLiteral node, Environment env) {
        String identifier = node.getValue();
        return env.search(identifier, true);
    }

}
