package dev.grcq.nitrolib.spigot.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public abstract class NitroBoard {
    protected final List<UUID> viewers = new ArrayList<>();
    private final Map<UUID, List<String>> currentLines = new HashMap<>();
    private final Map<UUID, String> currentTitle = new HashMap<>();
    private final Map<UUID, Boolean> initialized = new HashMap<>();

    // -----------------------------------------------------------------------
    // NMS reflection cache
    // -----------------------------------------------------------------------
    private static Class<?> nmsSB;            // net.minecraft.world.scores.Scoreboard
    private static Class<?> nmsTeam;          // net.minecraft.world.scores.PlayerTeam
    private static Class<?> nmsParams;        // ClientboundSetPlayerTeamPacket$Parameters
    private static Class<?> nmsPacket;        // ClientboundSetPlayerTeamPacket
    private static Class<?> nmsComponent;     // net.minecraft.network.chat.Component
    private static Constructor<?> ctorSB;     // Scoreboard()
    private static Constructor<?> ctorTeam;   // PlayerTeam(Scoreboard, String)
    private static Constructor<?> ctorParams; // Parameters(PlayerTeam)
    private static Constructor<?> ctorPacket; // Packet(String, int, Optional<Params>, Collection<String>)
    private static Method methodSetPrefix;    // PlayerTeam#setPlayerPrefix(Component)
    private static Method methodLiteral;      // Component#literal(String)
    private static boolean nmsReady = false;

    static {
        try {
            nmsSB       = Class.forName("net.minecraft.world.scores.Scoreboard");
            nmsTeam     = Class.forName("net.minecraft.world.scores.PlayerTeam");
            nmsParams   = Class.forName("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters");
            nmsPacket   = Class.forName("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket");
            nmsComponent = Class.forName("net.minecraft.network.chat.Component");

            ctorSB     = nmsSB.getDeclaredConstructor();
            ctorSB.setAccessible(true);
            ctorTeam   = nmsTeam.getDeclaredConstructor(nmsSB, String.class);
            ctorTeam.setAccessible(true);
            ctorParams = nmsParams.getDeclaredConstructor(nmsTeam);
            ctorParams.setAccessible(true);
            ctorPacket = nmsPacket.getDeclaredConstructor(String.class, int.class, Optional.class, Collection.class);
            ctorPacket.setAccessible(true);

            methodSetPrefix = nmsTeam.getMethod("setPlayerPrefix", nmsComponent);
            methodLiteral   = nmsComponent.getMethod("literal", String.class);

            nmsReady = true;
        } catch (Exception e) {
            // Running on old Spigot without these NMS classes – fall back to ProtocolLib path
        }
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public void show(Player player) {
        if (viewers.contains(player.getUniqueId())) return;
        viewers.add(player.getUniqueId());
        initialized.put(player.getUniqueId(), true);
        sendObjectiveCreate(player, getTitle(player));
        sendDisplayObjective(player);
        updatePlayer(player);
    }

    public void hide(Player player) {
        if (!viewers.remove(player.getUniqueId())) return;
        currentLines.remove(player.getUniqueId());
        currentTitle.remove(player.getUniqueId());
        initialized.remove(player.getUniqueId());
        sendObjectiveRemove(player);
    }

    public void update() {
        for (UUID uuid : new ArrayList<>(viewers)) {
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                updatePlayer(player);
            } else {
                viewers.remove(uuid);
                currentLines.remove(uuid);
                currentTitle.remove(uuid);
                initialized.remove(uuid);
            }
        }
    }

    protected void updatePlayer(Player player) {
        if (!initialized.getOrDefault(player.getUniqueId(), false)) return;

        String title = getTitle(player);
        List<String> lines = getLines(player);
        if (title == null) title = "";
        if (lines == null) lines = Collections.emptyList();

        UUID uuid = player.getUniqueId();

        // Title
        String oldTitle = currentTitle.get(uuid);
        if (!title.equals(oldTitle)) {
            sendObjectiveUpdate(player, title);
            currentTitle.put(uuid, title);
        }

        // Lines
        List<String> oldLines = currentLines.getOrDefault(uuid, Collections.emptyList());
        if (!lines.equals(oldLines)) {
            // Remove scores for lines that no longer exist
            for (int i = lines.size(); i < oldLines.size(); i++) {
                sendTeamRemove(player, getEntry(i));
                sendScoreRemove(player, getEntry(i));
            }

            for (int i = 0; i < lines.size(); i++) {
                String line   = lines.get(i);
                String entry  = getEntry(i);
                int    score  = lines.size() - i;
                String oldLine = i < oldLines.size() ? oldLines.get(i) : null;

                if (oldLine == null) {
                    sendTeamCreate(player, entry, line);
                    sendScoreUpdate(player, entry, score);
                } else if (!line.equals(oldLine)) {
                    sendTeamUpdate(player, entry, line);
                    if (oldLines.size() != lines.size()) sendScoreUpdate(player, entry, score);
                } else if (oldLines.size() != lines.size()) {
                    sendScoreUpdate(player, entry, score);
                }
            }

            currentLines.put(uuid, new ArrayList<>(lines));
        }
    }

    protected abstract String getTitle(Player player);
    protected abstract List<String> getLines(Player player);

    // -----------------------------------------------------------------------
    // Entry helpers
    // -----------------------------------------------------------------------

    private String getEntry(int index) {
        if (index < 0 || index > 15) return ChatColor.RESET.toString();
        return ChatColor.values()[index].toString() + ChatColor.RESET;
    }

    // -----------------------------------------------------------------------
    // Packet senders
    // -----------------------------------------------------------------------

    private void sendObjectiveCreate(Player p, String title) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, "nitroboard");
        packet.getIntegers().write(0, 0); // 0 = create
        try {
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));
        } catch (Exception e) {
            packet.getStrings().write(1, title.length() > 32 ? title.substring(0, 32) : title);
        }
        clearOptionals(packet);
        sendPacket(p, packet);
    }

    private void sendObjectiveUpdate(Player p, String title) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, "nitroboard");
        packet.getIntegers().write(0, 2); // 2 = update
        try {
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));
        } catch (Exception e) {
            packet.getStrings().write(1, title.length() > 32 ? title.substring(0, 32) : title);
        }
        clearOptionals(packet);
        sendPacket(p, packet);
    }

    private void sendObjectiveRemove(Player p) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, "nitroboard");
        packet.getIntegers().write(0, 1); // 1 = remove
        sendPacket(p, packet);
    }

    private void sendDisplayObjective(Player p) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        try {
            packet.getIntegers().write(0, 1); // sidebar
        } catch (Exception ex) {
            try {
                Object[] enums = packet.getModifier().getField(0).getType().getEnumConstants();
                for (Object f : enums) {
                    if (f.toString().toUpperCase().contains("SIDEBAR")) {
                        packet.getModifier().write(0, f);
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        packet.getStrings().write(0, "nitroboard");
        sendPacket(p, packet);
    }

    private void sendScoreUpdate(Player p, String entry, int score) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().write(0, entry);
        packet.getStrings().write(1, "nitroboard");
        packet.getIntegers().write(0, score);
        try {
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
        } catch (Exception ex) {
            if (packet.getIntegers().size() > 1) packet.getIntegers().write(1, 0);
        }
        clearOptionals(packet);
        sendPacket(p, packet);
    }

    private void sendScoreRemove(Player p, String entry) {
        try {
            Field field = PacketType.Play.Server.class.getDeclaredField("SCOREBOARD_SCORE_RESET");
            PacketType resetType = (PacketType) field.get(null);
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(resetType);
            packet.getStrings().write(0, entry);
            packet.getStrings().write(1, "nitroboard");
            sendPacket(p, packet);
            return;
        } catch (Exception ignored) {}
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().write(0, entry);
        try {
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
        } catch (Exception ex) {
            if (packet.getIntegers().size() > 1) packet.getIntegers().write(1, 1);
        }
        packet.getStrings().write(1, "nitroboard");
        sendPacket(p, packet);
    }

    // -----------------------------------------------------------------------
    // Team packets – built via NMS reflection to avoid ProtocolLib's broken
    // Optional<Parameters> default value on Paper 1.21+
    // -----------------------------------------------------------------------

    private void sendTeamCreate(Player p, String entry, String prefix) {
        sendTeamPacketNms(p, entry, prefix, 0, Collections.singletonList(entry));
    }

    private void sendTeamUpdate(Player p, String entry, String prefix) {
        sendTeamPacketNms(p, entry, prefix, 2, Collections.emptyList());
    }

    private void sendTeamRemove(Player p, String entry) {
        sendTeamPacketNms(p, entry, null, 4, Collections.singletonList(entry)); // 4 = LEAVE
    }

    /**
     * Constructs a ClientboundSetPlayerTeamPacket directly via NMS reflection.
     *
     * On Paper 1.21+, ProtocolLib's createPacket() leaves the Optional<Parameters>
     * field as a raw Object placeholder, which causes a ClassCastException in
     * ClientboundSetPlayerTeamPacket#write().  We work around this by:
     *   1. Creating a dummy NMS Scoreboard + PlayerTeam
     *   2. Setting the prefix on the team
     *   3. Constructing Parameters(PlayerTeam)
     *   4. Calling the packet's private (String, int, Optional<Parameters>, Collection) constructor
     *   5. Wrapping the result in a PacketContainer for ProtocolLib to send
     *
     * Falls back to the old ProtocolLib string/chat-component path on older versions.
     */
    private void sendTeamPacketNms(Player p, String entry, String prefix, int mode, Collection<String> players) {
        String teamName = "nb_" + entry.replace('§', 'x');

        if (nmsReady) {
            try {
                Optional<?> paramsOpt = Optional.empty();

                if (prefix != null) {
                    // 1. Dummy NMS scoreboard + team
                    Object sb   = ctorSB.newInstance();
                    Object team = ctorTeam.newInstance(sb, teamName);

                    // 2. Convert prefix string → NMS Component
                    Object component = nmsComponentFromString(prefix);
                    methodSetPrefix.invoke(team, component);

                    // 3. Parameters(PlayerTeam)
                    paramsOpt = Optional.of(ctorParams.newInstance(team));
                }

                // 4. Construct the packet
                Object nmsPacketInstance = ctorPacket.newInstance(teamName, mode, paramsOpt, players);

                // 5. Wrap in PacketContainer and send
                PacketContainer container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM, nmsPacketInstance);
                sendPacket(p, container);
                return;
            } catch (Exception ignored) {}
        }

        // ---- Legacy ProtocolLib fallback (1.12 – 1.20) ----
        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getStrings().write(0, teamName);
        packet.getIntegers().write(0, mode);
        if (prefix != null) {
            if (packet.getChatComponents().size() >= 3) {
                try {
                    packet.getChatComponents().write(0, WrappedChatComponent.fromText(""));
                    packet.getChatComponents().write(1, WrappedChatComponent.fromText(prefix));
                    packet.getChatComponents().write(2, WrappedChatComponent.fromText(""));
                } catch (Exception ignored) {}
            } else if (packet.getStrings().size() > 2) {
                String p1 = prefix.length() > 16 ? prefix.substring(0, 16) : prefix;
                String p2 = prefix.length() > 16 ? prefix.substring(16, Math.min(prefix.length(), 32)) : "";
                try { packet.getStrings().write(2, p1); } catch (Exception ignored) {}
                try { packet.getStrings().write(3, p2); } catch (Exception ignored) {}
            }
        }
        if (!players.isEmpty()) {
            try {
                packet.getSpecificModifier(Collection.class).write(0, players);
            } catch (Exception ignored) {}
        }
        sendPacket(p, packet);
    }

    /**
     * Converts a legacy § colour-coded string to an NMS Component.
     * Tries CraftChatMessage first (preserves colour codes), then falls back
     * to Component.literal() (plain text).
     */
    private Object nmsComponentFromString(String text) throws Exception {
        try {
            Class<?> craftChat = Class.forName("org.bukkit.craftbukkit.util.CraftChatMessage");
            Method fromString  = craftChat.getMethod("fromStringOrNull", String.class);
            Object result      = fromString.invoke(null, text);
            if (result != null) return result;
        } catch (Exception ignored) {}
        return methodLiteral.invoke(null, text);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void clearOptionals(PacketContainer packet) {
        try {
            List<?> optionals = packet.getSpecificModifier(Optional.class).getValues();
            for (int i = 0; i < optionals.size(); i++) {
                packet.getSpecificModifier(Optional.class).write(i, Optional.empty());
            }
        } catch (Exception ignored) {}
    }

    private void sendPacket(Player p, PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
    }
}
