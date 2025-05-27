package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.AssignmentExpr;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

public class AssignEval implements IEvaluator<AssignmentExpr> {

    @Override
    public RuntimeValue evaluate(AssignmentExpr node, Environment env) {
        RuntimeValue value = Evaluator.evaluate(node.getValue(), env);
        return env.assign(node.getIdentifier(), value);
    }
}
