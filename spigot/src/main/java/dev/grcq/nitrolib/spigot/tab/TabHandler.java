package dev.grcq.nitrolib.spigot.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.NitroSpigot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class TabHandler {

    @NotNull @Getter @Setter private static Function<@NotNull Player, @Nullable String> header;
    @NotNull @Getter @Setter private static Function<@NotNull Player, @Nullable String> footer;

    private static NitroSpigot nitro;
    private static boolean initialised;

    public static void init(NitroSpigot nitro) {
        if (initialised) return;
        initialised = true;

        header = (player) -> null;
        footer = (player) -> null;

        TabHandler.nitro = nitro;
    }

    public static void update(Player player) {
        if (!nitro.isProtocolLibEnabled()) {
            LogUtil.warn("ProtocolLib is required to use TabHandler.");
            return;
        }

        String h = header.apply(player);
        String f = footer.apply(player);
        if (h == null && f == null) return;

        ProtocolManager manager = nitro.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        if (h != null) packet.getChatComponents().write(0, WrappedChatComponent.fromText(h));
        if (f != null) packet.getChatComponents().write(1, WrappedChatComponent.fromText(f));

        try {
            manager.sendServerPacket(player, packet);
        } catch (Exception e) {
            LogUtil.handleException("Failed to update tab for " + player.getName(), e);
        }
    }
}
