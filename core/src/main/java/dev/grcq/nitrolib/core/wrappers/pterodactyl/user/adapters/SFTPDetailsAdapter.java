package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters;

import com.google.gson.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.SFTPDetails;

import java.lang.reflect.Type;

public class SFTPDetailsAdapter implements JsonDeserializer<SFTPDetails>, JsonSerializer<SFTPDetails> {

    @Override
    public SFTPDetails deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        return new SFTPDetails(
                object.get("ip").getAsString(),
                object.get("port").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(SFTPDetails sftpDetails, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("ip", sftpDetails.getIp());
        object.addProperty("port", sftpDetails.getPort());
        return object;
    }
}
