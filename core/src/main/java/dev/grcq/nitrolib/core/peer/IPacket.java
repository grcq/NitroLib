package dev.grcq.nitrolib.core.peer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.grcq.nitrolib.core.Constants;

public interface IPacket {

    String getPacketId();

    default JsonObject getPayload() {
        return JsonParser.parseString(Constants.GSON.toJson(this)).getAsJsonObject();
    }

}
