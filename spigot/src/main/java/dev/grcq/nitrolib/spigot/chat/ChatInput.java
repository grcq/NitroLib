package dev.grcq.nitrolib.spigot.chat;

import dev.grcq.nitrolib.spigot.NitroSpigot;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInput {

    protected static final Map<UUID, ChatInput> INPUTS = new HashMap<>();
    private static final Map<UUID, BukkitTask> TASKS = new HashMap<>();

    @Getter private final Callback callback;
    @Getter private final int timeout;
    @Getter private final String timeoutMessage;
    @Getter @Setter private boolean ignoreCancelled = true;

    public ChatInput(Callback callback) {
        this(callback, -1);
    }

    public ChatInput(Callback callback, int timeout) {
        this(callback, timeout, "&cYou took too long to respond.");
    }

    public ChatInput(Callback callback, int timeout, @Nullable String timeoutMessage) {
        this.callback = callback;
        this.timeout = timeout;
        this.timeoutMessage = timeoutMessage;
    }

    public void send(Player player) {
        INPUTS.put(player.getUniqueId(), this);

        if (this.timeout > 0) {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(NitroSpigot.getInstance().getSource(), () -> {
                if (this.timeoutMessage != null)
                    player.sendMessage(ChatUtil.format(this.timeoutMessage));
                INPUTS.remove(player.getUniqueId());
                TASKS.remove(player.getUniqueId());
            }, this.timeout * 20L);

            TASKS.put(player.getUniqueId(), task);
        }
    }

    protected void cancel(Player player) {
        INPUTS.remove(player.getUniqueId());
        BukkitTask task = TASKS.remove(player.getUniqueId());

        if (task != null) task.cancel();
    }

    @FunctionalInterface
    public interface Callback {
        void onInput(Player param1Player, String param1String);
    }
}
