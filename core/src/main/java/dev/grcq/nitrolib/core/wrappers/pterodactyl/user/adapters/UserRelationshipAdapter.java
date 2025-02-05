package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.adapters;

import com.google.gson.*;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.UserAllocation;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.UserRelationship;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserRelationshipAdapter implements JsonDeserializer<UserRelationship>, JsonSerializer<UserRelationship> {

    @Override
    public UserRelationship deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonElement.isJsonObject()) return null;
        JsonObject object = jsonElement.getAsJsonObject();
        JsonObject allocations = object.getAsJsonObject("allocations");
        JsonArray allocationsArray = allocations.getAsJsonArray("data");

        List<UserAllocation> userAllocations = new ArrayList<>();
        for (JsonElement element : allocationsArray) {
            if (!element.isJsonObject()) continue;

            JsonObject allocation = element.getAsJsonObject().getAsJsonObject("attributes");
            userAllocations.add(new UserAllocation(
                    allocation.get("id").getAsInt(),
                    allocation.get("ip").getAsString(),
                    allocation.get("port").getAsInt(),
                    allocation.get("ip_alias").isJsonNull() ? null : allocation.get("ip_alias").getAsString(),
                    allocation.get("notes").isJsonNull() ? null : allocation.get("notes").getAsString(),
                    !allocation.get("is_default").isJsonNull() && allocation.get("is_default").getAsBoolean()
            ));
        }

        return new UserRelationship(userAllocations);
    }

    @Override
    public JsonElement serialize(UserRelationship userRelationship, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        JsonObject allocations = new JsonObject();
        allocations.addProperty("object", "list");

        JsonArray data = new JsonArray();
        for (UserAllocation allocation : userRelationship.getAllocations()) {
            JsonObject allocationObject = new JsonObject();
            JsonObject attributes = new JsonObject();
            attributes.addProperty("id", allocation.getId());
            attributes.addProperty("ip", allocation.getIp());
            attributes.addProperty("port", allocation.getPort());
            attributes.addProperty("ip_alias", allocation.getIpAlias());
            attributes.addProperty("notes", allocation.getNotes());
            attributes.addProperty("is_default", allocation.isDefault());

            allocationObject.add("attributes", attributes);
            data.add(allocationObject);
        }

        allocations.add("data", data);
        object.add("allocations", allocations);
        return object;
    }
}
