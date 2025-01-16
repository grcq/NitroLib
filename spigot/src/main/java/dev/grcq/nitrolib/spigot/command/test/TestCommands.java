package dev.grcq.nitrolib.spigot.command.test;

import dev.grcq.nitrolib.spigot.command.annotations.Arg;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.command.annotations.Flag;
import dev.grcq.nitrolib.spigot.command.annotations.FlagValue;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommands {

    @Command("test")
    public void test(CommandSender sender) {
        sender.sendMessage("Test command executed!");
    }

    @Command("test hi")
    public void testHi(CommandSender sender) {
        sender.sendMessage("Hi!");
    }

    @Command("test hello")
    public void testHello(CommandSender sender, @Arg("name") String name) {
        sender.sendMessage("Hello, " + name + "!");
    }

    @Command("test player")
    public void testPlayer(CommandSender sender, @Arg("player") Player player) {
        sender.sendMessage("Player: " + player.getUniqueId());
    }

    @Command("test def")
    public void testDef(CommandSender sender, @Arg(value = "name", def = "default") String name) {
        sender.sendMessage("Name: " + name);
    }

    @Command("test opt")
    public void testOpt(CommandSender sender, @Arg(value = "name", required = false) String name) {
        sender.sendMessage("Name: " + name);
    }

    @Command("test wildcard")
    public void testWildcard(CommandSender sender, @Arg(value = "name", wildcard = true) String name) {
        sender.sendMessage("Name: " + name);
    }

    @Command("test multi")
    public void testMulti(CommandSender sender, @Arg("name") String name, @Arg(value = "age", def = "1") int age) {
        sender.sendMessage("Name: " + name + ", Age: " + age);
    }

    @Command("test flag")
    public void testFlag(CommandSender sender, @Flag("s") boolean silent, @Arg(value = "name") String name) {
        sender.sendMessage("Name: " + name + ", Silent: " + silent);
    }

    @Command("test pflag")
    public void testPFlag(CommandSender sender, @Flag("s") boolean silent, @Arg("player") Player player) {
        sender.sendMessage("Player: " + player.getUniqueId() + ", Silent: " + silent);
    }

    @Command("test flagval")
    public void testFlagVal(CommandSender sender, @FlagValue(name = "f", arg = "value") String value, @Arg(value = "name") String name) {
        sender.sendMessage("Name: " + name + ", Value: " + value);
    }


}
