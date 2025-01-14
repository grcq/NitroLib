package dev.grcq.nitrolib.spigot.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class ChatUtil {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message, Object... args) {
        return String.format(format(message), Lists.newArrayList(args).stream().map(Object::toString).map(ChatUtil::format).toArray());
    }

}
