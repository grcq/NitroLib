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
        return key + " " + operator + " " + (value instanceof String ? "'" + value + "'" : value);
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
