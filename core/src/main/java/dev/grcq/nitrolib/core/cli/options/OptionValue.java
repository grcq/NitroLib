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

}
