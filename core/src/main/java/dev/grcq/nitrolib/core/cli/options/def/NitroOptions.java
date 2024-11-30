package dev.grcq.nitrolib.core.cli.options.def;

import dev.grcq.nitrolib.core.cli.options.IOptions;
import dev.grcq.nitrolib.core.cli.options.Option;
import lombok.Getter;

@Getter
public class NitroOptions implements IOptions {

    @Option(names = "silent", shortNames = "s", description = "Silent mode")
    private boolean silent = false;

    @Option(names = "verbose", shortNames = "v", description = "Verbose mode")
    private boolean verbose = false;

    @Option(names = "debug", shortNames = "d", description = "Debug mode")
    private boolean debug = false;

}
