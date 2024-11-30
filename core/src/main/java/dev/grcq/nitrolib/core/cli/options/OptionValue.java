package dev.grcq.nitrolib.core.cli.options;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OptionValue {

    STRING(String.class),
    INTEGER(Integer.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    ARRAY(String[].class);

    private final Class<?> type;

    public Object cast(String value) {
        if (type == String.class) return value;
        if (type == Integer.class) return Integer.parseInt(value);
        if (type == Float.class) return Float.parseFloat(value);
        if (type == Double.class) return Double.parseDouble(value);
        if (type == Boolean.class) return Boolean.parseBoolean(value);
        if (type == String[].class) return value.split(",");
        return null;
    }

}
