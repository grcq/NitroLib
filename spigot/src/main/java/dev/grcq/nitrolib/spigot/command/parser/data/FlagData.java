package dev.grcq.nitrolib.spigot.command.parser.data;

import dev.grcq.nitrolib.spigot.command.annotations.Flag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;

@Data
@AllArgsConstructor
public class FlagData implements IData {

    private final String name;
    private final boolean value;

    @Override
    public Class<? extends Annotation> forType() {
        return Flag.class;
    }
}
