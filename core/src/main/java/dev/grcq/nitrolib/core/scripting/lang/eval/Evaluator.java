package dev.grcq.nitrolib.core.scripting.lang.eval;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.ASTNode;
import dev.grcq.nitrolib.core.scripting.lang.ast.Statement;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.*;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.FunctionStatement;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.Program;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.VariableDeclaration;
import dev.grcq.nitrolib.core.scripting.lang.eval.impl.*;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;

import java.util.HashMap;
import java.util.Map;

public class Evaluator {

    private static final Map<Class<? extends ASTNode>, IEvaluator<? extends ASTNode>> REGISTERED_EVALUATORS = new HashMap<>();

    static {
        REGISTERED_EVALUATORS.put(IdentifierLiteral.class, new IdentifierEval());
        REGISTERED_EVALUATORS.put(Statement.Block.class, new BlockEval());
        REGISTERED_EVALUATORS.put(StringLiteral.class, new StringEval());
        REGISTERED_EVALUATORS.put(BooleanLiteral.class, new BooleanEval());
        REGISTERED_EVALUATORS.put(NumberLiteral.class, new NumberEval());
        REGISTERED_EVALUATORS.put(NullLiteral.class, new NullEval());
        REGISTERED_EVALUATORS.put(FunctionCall.class, new FunctionCallEval());
        REGISTERED_EVALUATORS.put(AssignmentExpr.class, new AssignEval());
        REGISTERED_EVALUATORS.put(VariableDeclaration.class, new VariableEval());
        REGISTERED_EVALUATORS.put(FunctionStatement.class, new FunctionEval());
        REGISTERED_EVALUATORS.put(BinaryExpr.class, new BinaryEval());
    }

    public static RuntimeValue evaluate(ASTNode node, Environment env) {
        if (node instanceof Program) {
            RuntimeValue result = null;
            Program program = (Program) node;
            for (ASTNode child : program.getBody()) {
                result = evaluate(child, env);
            }
            return result;
        }

        IEvaluator<ASTNode> evaluator = (IEvaluator<ASTNode>) getEvaluator(node.getClass());
        if (evaluator == null) throw new IllegalArgumentException("No evaluator registered for class: " + node.getClass().getName());

        return evaluator.evaluate(node, env);
    }

    public static <T extends ASTNode> IEvaluator<T> getEvaluator(Class<T> clazz) {
        if (REGISTERED_EVALUATORS.containsKey(clazz)) return (IEvaluator<T>) REGISTERED_EVALUATORS.get(clazz);

        throw new IllegalArgumentException("No evaluator registered for class: " + clazz.getName());
    }

}