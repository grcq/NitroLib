package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.StringLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.StringValue;

public class StringEval implements IEvaluator<StringLiteral> {

    @Override
    public RuntimeValue evaluate(StringLiteral node, Environment env) {
        return new StringValue(node.getValue());
    }
}
