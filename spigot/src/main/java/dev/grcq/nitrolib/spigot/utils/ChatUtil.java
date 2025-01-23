package dev.grcq.nitrolib.spigot.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.List;

@UtilityClass
public class ChatUtil {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message, Object... args) {
        return String.format(format(message), Lists.newArrayList(args).stream().map(Object::toString).map(ChatUtil::format).toArray());
    }

    public static List<String> format(List<String> messages) {
        List<String> formatted = Lists.newArrayList();
        for (String message : messages) {
            formatted.add(format(message));
        }
        return formatted;
    }

    public static String[] format(String[] messages) {
        String[] formatted = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            formatted[i] = format(messages[i]);
        }
        return formatted;
    }

}
