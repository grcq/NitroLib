package dev.grcq.nitrolib.spigot.command.parser.data;

import dev.grcq.nitrolib.spigot.command.annotations.FlagValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;

@Data
@AllArgsConstructor
public class FlagValueData implements IData {

    private final String name;
    private final String argName;
    private final Object value;

    @Override
    public Class<? extends Annotation> forType() {
        return FlagValue.class;
    }
}
