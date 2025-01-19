package dev.grcq.nitrolib.core.wrappers.pterodactyl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.NitroLib;
import dev.grcq.nitrolib.core.utils.HttpUtil;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.admin.server.Server;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PteroAdmin {

    private final String apiEndpoint;
    private final String apiKey;

    private final Map<String, String> headers;

    public PteroAdmin(String apiEndpoint, String apiKey) {
        this.apiEndpoint = apiEndpoint + (apiEndpoint.endsWith("/") ? "" : "/") + "api/application";
        this.apiKey = apiKey;

        this.headers = new HashMap<>();
        this.headers.put("Authorization", "Bearer " + apiKey);
        this.headers.put("Content-Type", "application/json");
    }

    @Nullable
    public List<Server> getServers() {
        String endpoint = this.apiEndpoint + "/servers";
        JsonObject response = HttpUtil.getJson(endpoint, this.headers);
        if (response == null) return null;

        List<Server> servers = new ArrayList<>();
        JsonArray data = response.getAsJsonArray("data");
        for (int i = 0; i < data.size(); i++) {
            JsonObject server = data.get(i).getAsJsonObject().getAsJsonObject("attributes");
            servers.add(NitroLib.GSON.fromJson(server, Server.class));
        }

        return servers;
    }

    @Nullable
    public Server getServer(String id) {
        String endpoint = this.apiEndpoint + "/servers/" + id;
        JsonObject response = HttpUtil.getJson(endpoint, this.headers);
        if (response == null) return null;

        JsonObject server = response.getAsJsonObject("attributes");
        return NitroLib.GSON.fromJson(server, Server.class);
    }

}
