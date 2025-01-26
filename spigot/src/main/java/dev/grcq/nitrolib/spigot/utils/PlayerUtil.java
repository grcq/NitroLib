package dev.grcq.nitrolib.spigot.utils;

import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.utils.HttpUtil;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    public static JsonObject getProfileSigned(UUID uuid) {
        String endpoint = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false";

        JsonObject object = HttpUtil.getJson(endpoint, new HashMap<>());
        if (object == null) return new JsonObject();

        return object;
    }

    public static UUID getUUID(String name) {
        String endpoint = "https://api.mojang.com/users/profiles/minecraft/" + name;

        JsonObject object = HttpUtil.getJson(endpoint, new HashMap<>());
        if (object == null) return null;

        return UUID.fromString(object.get("id").getAsString());
    }

}
