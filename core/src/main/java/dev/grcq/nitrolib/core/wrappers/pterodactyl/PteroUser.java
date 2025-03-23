package dev.grcq.nitrolib.core.wrappers.pterodactyl;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import dev.grcq.nitrolib.core.utils.HttpUtil;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    @Nullable
    public UserServer getServer(String id) {
        id = id.split("-")[0]; // allows full UUIDs
        String endpoint = this.apiEndpoint + "/servers/" + id;
        JsonObject response = HttpUtil.getJson(endpoint, this.headers);
        if (response == null) return null;

        return GSON.fromJson(response.getAsJsonObject("attributes"), UserServer.class);
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

    public void sendPowerAction(UserServer server, PowerAction action) {
        this.sendPowerAction(server.getIdentifier(), action);
    }

    public void sendPowerAction(String id, PowerAction action) {
        String endpoint = this.apiEndpoint + "/servers/" + id + "/power";
        JsonObject object = new JsonObject();
        object.addProperty("signal", action.getAction());
        HttpUtil.postJson(endpoint, this.headers, object.toString());
    }

    public void sendCommand(UserServer server, String command) {
        this.sendCommand(server.getIdentifier(), command);
    }

    public void sendCommand(String id, String command) {
        String endpoint = this.apiEndpoint + "/servers/" + id + "/command";
        JsonObject object = new JsonObject();
        object.addProperty("command", command);
        HttpUtil.postJson(endpoint, this.headers, object.toString());
    }
}
