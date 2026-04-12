package dev.grcq.nitrolib.spigot.chat;

import dev.grcq.nitrolib.spigot.events.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener {

    @Event(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatInput chatInput = ChatInput.INPUTS.get(player.getUniqueId());
        if (chatInput == null) return;

        event.setCancelled(true);
        String message = event.getMessage();
        chatInput.cancel(player);
        chatInput.getCallback().onInput(player, message);
    }

}
