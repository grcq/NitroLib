package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.FunctionStatement;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.FunctionValue;

public class FunctionEval implements IEvaluator<FunctionStatement> {

    @Override
    public RuntimeValue evaluate(FunctionStatement node, Environment env) {
        String name = node.getName();
        String[] params = node.getParameters();
        Statement.Block body = node.getBody();

        return env.declare(name, true, new FunctionValue(params, (v) -> {
            if (v.length != params.length) {
                env.throwError("Function '" + name + "' expects " + params.length + " arguments, but got " + v.length);
            }

            Environment functionEnv = new Environment(env);
            for (int i = 0; i < params.length; i++) {
                functionEnv.declare(params[i], false, v[i]);
            }

            return Evaluator.evaluate(body, functionEnv);
        }));
    }
}
