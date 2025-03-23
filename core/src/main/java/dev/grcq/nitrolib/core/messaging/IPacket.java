package dev.grcq.nitrolib.core.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.grcq.nitrolib.core.NitroLib;

public interface IPacket {

    String getIdentifier();
    default JsonElement getPayload() {
        return JsonParser.parseString(NitroLib.GSON.toJson(this));
    }

    static IPacket deserialize(String body) {
        JsonElement json = JsonParser.parseString(body);
        if (!json.isJsonObject()) throw new IllegalArgumentException("Packet must be a JSON object");

        JsonObject obj = json.getAsJsonObject();
        if (!obj.has("identifier")) throw new IllegalArgumentException("Packet must have an 'identifier' field");
        if (!obj.has("payload")) throw new IllegalArgumentException("Packet must have a 'payload' field");

        String identifier = obj.get("identifier").getAsString();
        JsonElement payload = obj.get("payload");
        return new IPacket() {
            @Override
            public String getIdentifier() {
                return identifier;
            }

            @Override
            public JsonElement getPayload() {
                return payload;
            }
        };

    }
}
