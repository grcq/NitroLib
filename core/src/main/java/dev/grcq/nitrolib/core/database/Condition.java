package dev.grcq.nitrolib.core.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Condition {

    private final String key;
    private final String operator;
    private final Object value;
    private boolean nextIsOr = false;

    public String toString() {
        return key + " " + operator + " " + (value instanceof String || value instanceof Character ? "'" + value.toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"") + "'" : value);
    }

    public static Condition of(String key, String operator, Object value) {
        return new Condition(key, operator, value);
    }

    public static Condition of(String key, String operator, Object value, boolean nextIsOr) {
        return new Condition(key, operator, value, nextIsOr);
    }

    public static Condition eq(String key, Object value) {
        return of(key, Operators.EQUALS, value);
    }

    public static Condition neq(String key, Object value) {
        return of(key, Operators.NOT_EQUALS, value);
    }

    public static Condition gt(String key, Object value) {
        return of(key, Operators.GREATER_THAN, value);
    }

    public static Condition lt(String key, Object value) {
        return of(key, Operators.LESS_THAN, value);
    }

    public static Condition gte(String key, Object value) {
        return of(key, Operators.GREATER_THAN_OR_EQUALS, value);
    }

    public static Condition lte(String key, Object value) {
        return of(key, Operators.LESS_THAN_OR_EQUALS, value);
    }

    public static Condition like(String key, Object value) {
        return of(key, Operators.LIKE, value);
    }

    public static Condition notLike(String key, Object value) {
        return of(key, Operators.NOT_LIKE, value);
    }

    public static Condition in(String key, Object value) {
        return of(key, Operators.IN, value);
    }

    public static Condition notIn(String key, Object value) {
        return of(key, Operators.NOT_IN, value);
    }

    public interface Operators {
        String EQUALS = "=";
        String NOT_EQUALS = "!=";
        String GREATER_THAN = ">";
        String LESS_THAN = "<";
        String GREATER_THAN_OR_EQUALS = ">=";
        String LESS_THAN_OR_EQUALS = "<=";
        String LIKE = "LIKE";
        String NOT_LIKE = "NOT LIKE";
        String IN = "IN";
        String NOT_IN = "NOT IN";
    }
}
