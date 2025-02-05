package dev.grcq.nitrolib.core.wrappers.pterodactyl;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import dev.grcq.nitrolib.core.utils.HttpUtil;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PteroUser {

    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ServerState.class, new ServerStateAdapter())
            .registerTypeAdapter(UserRelationship.class, new UserRelationshipAdapter())
            .registerTypeAdapter(UserFeatureLimits.class, new FeatureLimitsAdapter())
            .registerTypeAdapter(UserServerLimits.class, new ServerLimitsAdapter())
            .registerTypeAdapter(SFTPDetails.class, new SFTPDetailsAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();

    private final String apiEndpoint;
    private final String apiKey;

    private final Map<String, String> headers;

    public PteroUser(String apiEndpoint, String apiKey) {
        this.apiEndpoint = apiEndpoint + (apiEndpoint.endsWith("/") ? "" : "/") + "api/client";
        this.apiKey = apiKey;

        this.headers = new HashMap<>();
        this.headers.put("Authorization", "Bearer " + apiKey);
        this.headers.put("Content-Type", "application/json");
    }

    public List<UserServer> getServers() {
        String endpoint = this.apiEndpoint + "/";
        JsonObject response = HttpUtil.getJson(endpoint, this.headers);
        if (response == null) return ImmutableList.of();

        List<UserServer> servers = new ArrayList<>();
        JsonArray data = response.getAsJsonArray("data");
        for (JsonElement element : data) {
            if (!element.isJsonObject()) continue;
            JsonObject object = element.getAsJsonObject();
            if (!object.get("object").getAsString().equals("server") || !object.has("attributes")) continue;

            UserServer server = GSON.fromJson(object.get("attributes"), UserServer.class);
            servers.add(server);
        }

        return ImmutableList.copyOf(servers);
    }

    public ServerState getServerState(UserServer server) {
        return this.getServerState(server.getIdentifier());
    }

    public ServerState getServerState(String id) {
        String endpoint = this.apiEndpoint + "/servers/" + id + "/resources";
        JsonObject response = HttpUtil.getJson(endpoint, this.headers);
        if (response == null) return null;

        return GSON.fromJson(response.getAsJsonObject("attributes").get("current_state"), ServerState.class);
    }
}
