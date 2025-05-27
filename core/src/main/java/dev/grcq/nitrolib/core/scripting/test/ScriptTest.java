package dev.grcq.nitrolib.core.scripting.test;

import dev.grcq.nitrolib.core.scripting.ScriptManager;
import org.junit.Test;

public class ScriptTest {

    @Test
    public void testScript() {
        ScriptManager scriptManager = new ScriptManager();
        scriptManager.execute("var abc = 5");
    }

    @Test
    public void testFile() {

    }

}
