package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.VariableDeclaration;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

public class VariableEval implements IEvaluator<VariableDeclaration> {

    @Override
    public RuntimeValue evaluate(VariableDeclaration node, Environment env) {
        String name = node.getName();
        RuntimeValue value = Evaluator.evaluate(node.getValue(), env);
        return env.declare(name, node.isConstant(), value);
    }
}
