package dev.grcq.nitrolib.core;

import dev.grcq.nitrolib.core.annotations.Cached;
import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import lombok.Getter;

public class TestClass {

    //@Inject
    @Getter
    private NitroOptions options;

    @Cached
    public String test(int i) {
        System.out.println("Called test with " + i);
        return "Number: " + i;
    }

}
