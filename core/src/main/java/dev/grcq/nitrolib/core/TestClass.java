package dev.grcq.nitrolib.core;

import dev.grcq.nitrolib.core.annotations.Cached;
import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.annotations.Singleton;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import lombok.Getter;

@Singleton
public class TestClass {

    @Cached
    public String test(int i) {
        System.out.println("Called test with " + i);
        return "Number: " + i;
    }

    public void test() {
        System.out.println("Hello World!");
    }
}

