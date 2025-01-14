package dev.grcq.nitrolib.spigot.command.parser.data;

import dev.grcq.nitrolib.spigot.command.annotations.Arg;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;

@Data
@AllArgsConstructor
public class ArgData implements IData {

    private final String name;
    private final Object value;
    private final boolean required;

    @Override
    public Class<? extends Annotation> forType() {
        return Arg.class;
    }
}
