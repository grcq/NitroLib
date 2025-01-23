package dev.grcq.nitrolib.spigot.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageBuilder {

    private final TextComponent component;

    public MessageBuilder() {
        this.component = new TextComponent();
    }

    public MessageBuilder text(String... text) {
        return text(" ", text);
    }

    public MessageBuilder text(String del, String... text) {
        this.component.setText(String.join(del, ChatUtil.format(text)));
        return this;
    }

    public MessageBuilder hover(String... text) {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatUtil.format(Arrays.asList(text)).stream()
                .map(TextComponent::new).toArray(TextComponent[]::new));
        this.component.setHoverEvent(hoverEvent);
        return this;
    }

    public MessageBuilder hover(TextComponent... components) {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, components);
        this.component.setHoverEvent(hoverEvent);
        return this;
    }

    public MessageBuilder hover(HoverEvent.Action action, TextComponent... components) {
        HoverEvent hoverEvent = new HoverEvent(action, components);
        this.component.setHoverEvent(hoverEvent);
        return this;
    }

    public MessageBuilder click(String command) {
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        this.component.setClickEvent(clickEvent);
        return this;
    }

    public MessageBuilder click(ClickEvent.Action action, String value) {
        ClickEvent clickEvent = new ClickEvent(action, value);
        this.component.setClickEvent(clickEvent);
        return this;
    }

    public MessageBuilder color(String color) {
        this.component.setColor(ChatColor.valueOf(color.toUpperCase()));
        return this;
    }

    public MessageBuilder color(ChatColor color) {
        this.component.setColor(color);
        return this;
    }

    public MessageBuilder bold() {
        this.component.setBold(true);
        return this;
    }

    public MessageBuilder italic() {
        this.component.setItalic(true);
        return this;
    }

    public MessageBuilder underlined() {
        this.component.setUnderlined(true);
        return this;
    }

    public MessageBuilder strikethrough() {
        this.component.setStrikethrough(true);
        return this;
    }

    public MessageBuilder obfuscated() {
        this.component.setObfuscated(true);
        return this;
    }

    public MessageBuilder reset() {
        this.component.setBold(false);
        this.component.setItalic(false);
        this.component.setUnderlined(false);
        this.component.setStrikethrough(false);
        this.component.setObfuscated(false);
        this.component.setColor(ChatColor.RESET);
        this.component.setClickEvent(null);
        this.component.setHoverEvent(null);
        return this;
    }

    public MessageBuilder child(MessageBuilder... builders) {
        for (MessageBuilder builder : builders) {
            this.component.addExtra(builder.build());
        }
        return this;
    }

    public MessageBuilder child(TextComponent... components) {
        for (TextComponent component : components) {
            this.component.addExtra(component);
        }
        return this;
    }

    public TextComponent build() {
        return this.component;
    }

    public MessageBuilder send(CommandSender sender) {
        sender.spigot().sendMessage(this.component);
        return this;
    }
}
