package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.FunctionCall;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.FunctionValue;

import java.util.Arrays;

public class FunctionCallEval implements IEvaluator<FunctionCall> {

    @Override
    public RuntimeValue evaluate(FunctionCall node, Environment env) {
        RuntimeValue callee = Evaluator.evaluate(node.getCallee(), env);
        RuntimeValue[] arguments = Arrays.stream(node.getArguments()).map(arg -> Evaluator.evaluate(arg, env)).toArray(RuntimeValue[]::new);
        if (!(callee instanceof FunctionValue)) env.throwError("Cannot call non-function value");

        FunctionValue function = (FunctionValue) callee;
        return function.getFunction().call(arguments);
    }
}
