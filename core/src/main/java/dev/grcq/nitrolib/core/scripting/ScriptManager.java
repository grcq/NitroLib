package dev.grcq.nitrolib.core.scripting;

import dev.grcq.nitrolib.core.scripting.lang.Environment;
import dev.grcq.nitrolib.core.scripting.lang.ast.impl.statements.Program;
import dev.grcq.nitrolib.core.scripting.lang.eval.Evaluator;
import dev.grcq.nitrolib.core.scripting.lang.parser.Lexer;
import dev.grcq.nitrolib.core.scripting.lang.ast.ASTNode;
import dev.grcq.nitrolib.core.scripting.lang.parser.Parser;
import dev.grcq.nitrolib.core.scripting.lang.parser.Token;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.impl.FunctionValue;
import dev.grcq.nitrolib.core.utils.LogUtil;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ScriptManager class is responsible for managing the execution of scripts.
 * It provides methods to load scripts, execute them, and manage the environment in which they run.
 * <br>
 * <b>Note:</b> This class is marked as experimental, under development, and may change in future versions.
 */
@Experimental
public class ScriptManager {

    private final Environment environment;
    private final Map<String, List<ASTNode>> loadedScripts;

    public ScriptManager() {
        this.environment = new Environment(null, "Global");
        this.loadedScripts = new HashMap<>();

        registerFunction("print", (args) -> {
            if (args.length == 0) {
                LogUtil.fatal("print() requires at least one argument");
                return null;
            }

            System.out.println(args[0].getValue());
            return null;
        });
    }

    public void registerFunction(String name, FunctionValue.Function function) {
        environment.declare(name, true, new FunctionValue(new String[0], function));
    }

    public void declareVariable(String name, boolean constant, RuntimeValue value) {
        environment.declare(name, constant, value);
    }

    public void loadScript(String file, List<ASTNode> nodes) {
        loadedScripts.put(file, nodes);
    }

    public void execute(String input) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        Evaluator.evaluate(program, environment);
    }
}
