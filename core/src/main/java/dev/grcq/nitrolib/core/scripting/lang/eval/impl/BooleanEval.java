package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.BooleanLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.BooleanValue;

public class BooleanEval implements IEvaluator<BooleanLiteral> {

    @Override
    public RuntimeValue evaluate(BooleanLiteral node, Environment env) {
        return new BooleanValue(node.isValue());
    }
}
