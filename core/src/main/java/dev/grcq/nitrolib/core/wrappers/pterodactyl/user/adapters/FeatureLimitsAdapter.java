package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters;

import com.google.gson.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.UserFeatureLimits;

import java.lang.reflect.Type;

public class FeatureLimitsAdapter implements JsonDeserializer<UserFeatureLimits>, JsonSerializer<UserFeatureLimits> {

    @Override
    public UserFeatureLimits deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        return new UserFeatureLimits(
                object.get("databases").getAsInt(),
                object.get("allocations").getAsInt(),
                object.get("backups").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(UserFeatureLimits userFeatureLimits, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("databases", userFeatureLimits.getDatabases());
        object.addProperty("allocations", userFeatureLimits.getAllocations());
        object.addProperty("backups", userFeatureLimits.getBackups());
        return object;
    }
}
