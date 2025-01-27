package dev.grcq.nitrolib.spigot.utils;

import com.mojang.authlib.GameProfile;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@UtilityClass
public class NMSUtil {

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    @Nullable
    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            LogUtil.verbose("ERROR: Failed to get OBC class " + name);
            return null;
        }
    }

    @Nullable
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            LogUtil.verbose("ERROR: Failed to get NMS class " + name);
            return null;
        }
    }

    public static int getVersionInt() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (version.split("\\.").length == 2) version += ".0";
        return Integer.parseInt(version.replace(".", ""));
    }

    public static void sendPacket(Object packet, Object player) {
        try {
            Class<?> craftPlayerClass = getOBCClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);

            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
            LogUtil.handleException(e);
        }
    }

    public static Object getMCServer() {
        try {
            Class<?> mcServerClass = getNMSClass("MinecraftServer");
            return mcServerClass.getMethod("getServer").invoke(null);
        } catch (Exception e) {
            LogUtil.handleException(e);
            return null;
        }
    }

    public static Object getCraftServer() {
        try {
            Class<?> craftServerClass = getOBCClass("CraftServer");
            Object craftServer = craftServerClass.cast(Bukkit.getServer());
            return craftServerClass.getMethod("getServer").invoke(craftServer);
        } catch (Exception e) {
            LogUtil.handleException(e);
            return null;
        }
    }

    public static Object getWorldServer(Object world) {
        try {
            Class<?> craftWorldClass = getOBCClass("CraftWorld");
            Object craftWorld = craftWorldClass.cast(world);
            return craftWorldClass.getMethod("getHandle").invoke(craftWorld);
        } catch (Exception e) {
            LogUtil.handleException(e);
            return null;
        }
    }

    public static float[] getTicksPerSecond() {
        try {
            Object server = getMCServer();
            Field tpsField = server.getClass().getField("recentTps");
            double[] tps = (double[]) tpsField.get(server);

            return new float[] {(float) Math.round(tps[0] * 100) / 100, (float) Math.round(tps[1] * 100) / 100, (float) Math.round(tps[2] * 100) / 100};
        } catch (Exception e) {
            LogUtil.handleException(e);
            return new float[] {0f, 0f, 0f};
        }
    }

    public static Object createEntityPlayer(Object world, String name) {
        try {
            Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
            Class<?> worldServerClass = getNMSClass("WorldServer");
            Object worldServer = worldServerClass.cast(world);

            return entityPlayerClass.getConstructor(worldServerClass, getNMSClass("GameProfile")).newInstance(worldServer, new com.mojang.authlib.GameProfile(null, name));
        } catch (Exception e) {
            LogUtil.handleException(e);
            return null;
        }
    }

    public static Object getWorldServer(World world) {
        try {
            Class<?> craftWorldClass = getOBCClass("CraftWorld");
            Object craftWorld = craftWorldClass.cast(world);
            return craftWorldClass.getDeclaredMethod("getHandle").invoke(craftWorld);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object createEntityPlayer(Object mcServer, Object worldServer, GameProfile gameProfile, Location location) {
        try {
            Class<?> interactManagerClass = getNMSClass("PlayerInteractManager");
            Object interactManager = interactManagerClass.getDeclaredConstructors()[0]
                    .newInstance(worldServer);

            Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
            Object entityPlayer = entityPlayerClass.getDeclaredConstructor(
                    getNMSClass("MinecraftServer"),
                    getNMSClass("WorldServer"),
                    GameProfile.class,
                    interactManagerClass
            ).newInstance(mcServer, worldServer, gameProfile, interactManager);

            entityPlayerClass
                    .getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            return entityPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getEntityPlayer(Player player) {
        try {
            Class<?> craftPlayerClass = getOBCClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            return craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
