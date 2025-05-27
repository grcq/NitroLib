package dev.grcq.nitrolib.core.scripting.lang;

import dev.grcq.nitrolib.core.scripting.ScriptManager;
import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.utils.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Environment {

    @Nullable
    private final Environment parent;

    @Nullable
    private final String name;

    private final Map<String, RuntimeValue> variables = new HashMap<>();
    private final Set<String> constants = new HashSet<>();

    public Environment(@Nullable Environment parent) {
        this(parent, "Global");
    }

    public Environment(@Nullable Environment parent, String name) {
        this.parent = parent;
        this.name = name;
    }
    public RuntimeValue search(String name, boolean deep) {
        RuntimeValue value = variables.get(name);
        if (value != null) return value;
        if (deep && parent != null) return parent.search(name, true);

        throwError("Variable not found: " + name);
        return null;
    }

    public RuntimeValue declare(String name, boolean constant, RuntimeValue value) {
        if (variables.containsKey(name)) throwError("Variable already declared: " + name);
        if (constant) {
            if (constants.contains(name)) throwError("Cannot redefine constant variable: " + name);
            constants.add(name);
        }

        return variables.put(name, value);
    }

    public RuntimeValue assign(String name, RuntimeValue value) {
        if (variables.containsKey(name)) {
            if (constants.contains(name)) throwError("Cannot assign to constant variable: " + name);
            return variables.put(name, value);
        }

        if (parent != null) return parent.assign(name, value);

        throwError("Variable not found: " + name);
        return null;
    }

    public void throwError(String message) {
        LogUtil.fatal("Error in %s: %s", name, message);
    }
}
