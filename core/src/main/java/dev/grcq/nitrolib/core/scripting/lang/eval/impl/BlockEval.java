package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

public class BlockEval implements IEvaluator<Statement.Block> {

    @Override
    public RuntimeValue evaluate(Statement.Block node, Environment env) {
        Environment blockEnv = new Environment(env);
        RuntimeValue result = null;
        for (Statement statement : node.getStatements()) {
            result = Evaluator.evaluate(statement, blockEnv);
        }
        return result;
    }
}
