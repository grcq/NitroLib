package dev.grcq.nitrolib.core.tests.options;

import dev.grcq.nitrolib.core.cli.options.IOptions;
import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.OptionParser;

public class MyOptions implements IOptions {

    @Option(names = "name", shortNames = "n", description = "The name of the user")
    private String name;

    public static void main(String[] args) {
        MyOptions options = new MyOptions();
        OptionParser.parse(options, new String[] {"--name", "John"});

        System.out.println("Name: " + options.name);
    }
}
