package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.NumberLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.NumberValue;

public class NumberEval implements IEvaluator<NumberLiteral> {

    @Override
    public RuntimeValue evaluate(NumberLiteral node, Environment env) {
        return new NumberValue(node.getValue());
    }
}
