package dev.grcq.nitrolib.core.cli;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class CliCommand {

    private final String name;
    @Nullable
    private String description;

    public CliCommand(final String name) {
        this.name = name;
        this.description = null;
    }

    public CliCommand(final String name, @Nullable final String description) {
        this.name = name;
        this.description = description;
    }


}
