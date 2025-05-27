package dev.grcq.nitrolib.core.scripting.lang.eval.impl;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.BinaryExpr;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.expressions.IdentifierLiteral;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.eval.IEvaluator;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.BooleanValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.NumberValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.StringValue;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class BinaryEval implements IEvaluator<BinaryExpr> {

    private static final Map<String, Map<List<ValueType>, BiFunction<RuntimeValue, RuntimeValue, RuntimeValue>>> OPERATORS = new HashMap<>();

    static {
        // Initialize maps for each operator
        OPERATORS.put("+", new HashMap<>());
        OPERATORS.put("-", new HashMap<>());
        OPERATORS.put("*", new HashMap<>());
        OPERATORS.put("/", new HashMap<>());
        OPERATORS.put("%", new HashMap<>());
        OPERATORS.put("&&", new HashMap<>());
        OPERATORS.put("||", new HashMap<>());

        registerOperator("&&", ValueType.BOOLEAN, ValueType.BOOLEAN, (l, r) -> new BooleanValue((boolean) l.getValue() && (boolean) r.getValue()));
        registerOperator("||", ValueType.BOOLEAN, ValueType.BOOLEAN, (l, r) -> new BooleanValue((boolean) l.getValue() || (boolean) r.getValue()));

        registerOperator("+", ValueType.NUMBER, ValueType.NUMBER, (l, r) -> new NumberValue((double) l.getValue() + (double) r.getValue()));
        registerOperator("-", ValueType.NUMBER, ValueType.NUMBER, (l, r) -> new NumberValue((double) l.getValue() - (double) r.getValue()));
        registerOperator("*", ValueType.NUMBER, ValueType.NUMBER, (l, r) -> new NumberValue((double) l.getValue() * (double) r.getValue()));
        registerOperator("/", ValueType.NUMBER, ValueType.NUMBER, (l, r) -> {
            if ((double) r.getValue() == 0) return new NumberValue(Double.MAX_VALUE);
            return new NumberValue((double) l.getValue() / (double) r.getValue());
        });
        registerOperator("%", ValueType.NUMBER, ValueType.NUMBER, (l, r) -> {
            if ((double) r.getValue() == 0) return new NumberValue(Double.MAX_VALUE);
            return new NumberValue((double) l.getValue() % (double) r.getValue());
        });

        registerOperator("+", ValueType.BOOLEAN, ValueType.NUMBER, (l, r) -> new NumberValue((double) l.getValue() + ((boolean) r.getValue() ? 1 : 0)));
        registerOperator("+", ValueType.NUMBER, ValueType.BOOLEAN, (l, r) -> new NumberValue(((boolean) l.getValue() ? 1 : 0) + (double) r.getValue()));
        registerOperator("-", ValueType.BOOLEAN, ValueType.NUMBER, (l, r) -> new NumberValue(((boolean) l.getValue() ? 1 : 0) - (double) r.getValue()));
        registerOperator("-", ValueType.NUMBER, ValueType.BOOLEAN, (l, r) -> new NumberValue((double) l.getValue() - ((boolean) r.getValue() ? 1 : 0)));

        registerOperator("+", ValueType.STRING, ValueType.STRING, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.STRING, ValueType.NUMBER, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.STRING, ValueType.BOOLEAN, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.STRING, ValueType.ARRAY, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.STRING, ValueType.NULL, (l, r) -> new StringValue(l.getValue().toString() + "null"));

        registerOperator("+", ValueType.NUMBER, ValueType.STRING, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.BOOLEAN, ValueType.STRING, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.ARRAY, ValueType.STRING, (l, r) -> new StringValue(l.getValue().toString() + r.getValue()));
        registerOperator("+", ValueType.NULL, ValueType.STRING, (l, r) -> new StringValue("null" + r.getValue()));
    }

    @Override
    public RuntimeValue evaluate(BinaryExpr node, Environment env) {
        RuntimeValue left = Evaluator.evaluate(node.getLeft(), env);
        RuntimeValue right = Evaluator.evaluate(node.getRight(), env);

        Map<List<ValueType>, BiFunction<RuntimeValue, RuntimeValue, RuntimeValue>> operatorMap = OPERATORS.get(node.getOperator());

        if (operatorMap != null) {
            BiFunction<RuntimeValue, RuntimeValue, RuntimeValue> operation = operatorMap.get(Lists.newArrayList(left.getType(), right.getType()));
            if (operation != null) return operation.apply(left, right);
        }

        if (node.getLeft() instanceof IdentifierLiteral) {
            String name = ((IdentifierLiteral) node.getLeft()).getValue();
            List<String> operators = Lists.newArrayList("+=", "-=", "*=", "/=", "%=");
            if (operators.contains(node.getOperator())) {
                if (!node.getOperator().equals("+=") && left.getType() != ValueType.NUMBER && right.getType() != ValueType.NUMBER) LogUtil.error("Unsupported operation: " + left.getType() + " " + node.getOperator() + " " + right.getType(), 1);

                RuntimeValue result = Evaluator.evaluate(new BinaryExpr(new IdentifierLiteral(name), node.getOperator().substring(0, 1), node.getRight()), env);
                return env.assign(name, result);
            }
        }

        LogUtil.error("Unsupported operation: " + left.getType() + " " + node.getOperator() + " " + right.getType(), 1);
        return null;
    }

    private static void registerOperator(String operator, ValueType t1, ValueType t2, BiFunction<RuntimeValue, RuntimeValue, RuntimeValue> function) {
        OPERATORS.get(operator).put(Lists.newArrayList(t1, t2), function);
    }

    @FunctionalInterface
    private interface DoubleBinaryOperator {
        double applyAsDouble(Object left, Object right);
    }
}