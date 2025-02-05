package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters;

import com.google.gson.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.UserServerLimits;

import java.lang.reflect.Type;

public class ServerLimitsAdapter implements JsonDeserializer<UserServerLimits>, JsonSerializer<UserServerLimits> {

    @Override
    public UserServerLimits deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        return new UserServerLimits(
                object.get("memory").getAsInt(),
                object.get("swap").getAsInt(),
                object.get("disk").getAsInt(),
                object.get("io").getAsInt(),
                object.get("cpu").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(UserServerLimits userServerLimits, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("memory", userServerLimits.getMemory());
        object.addProperty("swap", userServerLimits.getSwap());
        object.addProperty("disk", userServerLimits.getDisk());
        object.addProperty("io", userServerLimits.getIo());
        object.addProperty("cpu", userServerLimits.getCpu());
        return object;
    }
}
