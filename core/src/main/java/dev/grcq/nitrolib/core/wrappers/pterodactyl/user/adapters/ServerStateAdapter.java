package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters;

import com.google.gson.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.ServerState;

import java.lang.reflect.Type;

public class ServerStateAdapter implements JsonDeserializer<ServerState>, JsonSerializer<ServerState> {

    @Override
    public ServerState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ServerState.fromIdentifier(json.getAsString());
    }

    @Override
    public JsonElement serialize(ServerState src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getIdentifier());
    }

}
