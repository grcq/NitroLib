package dev.grcq.nitrolib.core.wrappers.pterodactyl.admin.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Server {

    @Getter private final int id;
    @Getter private final String external_id;
    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final String description;

    @Getter private final boolean suspended;
    @Getter private final ServerLimits limits;
    private final FeatureLimits feature_limits;

    @Getter private final int user;
    @Getter private final int node;
    @Getter private final int allocation;
    @Getter private final int nest;
    @Getter private final int egg;
    @Getter private final String pack;

    @Getter private final ServerContainer container;
    private final Date created_at;
    private final Date updated_at;

    public FeatureLimits getFeatureLimits() {
        return feature_limits;
    }

}
