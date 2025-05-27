package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.NullLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.NullValue;

public class NullEval implements IEvaluator<NullLiteral> {

    @Override
    public RuntimeValue evaluate(NullLiteral node, Environment env) {
        return new NullValue();
    }
}
