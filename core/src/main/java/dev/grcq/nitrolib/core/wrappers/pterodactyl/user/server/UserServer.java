package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.*;

import java.util.List;
import java.util.UUID;

@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserServer {

    private final boolean server_owner;
    @Getter private final String identifier;
    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final String node;
    @Getter private final String description;

    private final SFTPDetails sftp_details;
    @Getter private final UserServerLimits limits;
    private final UserFeatureLimits feature_limits;

    private final boolean is_suspended;
    private final boolean is_installing;

    @Getter private final UserRelationship relationships;

    public boolean isServerOwner() {
        return server_owner;
    }

    public boolean isSuspended() {
        return is_suspended;
    }

    public boolean isInstalling() {
        return is_installing;
    }

    public SFTPDetails getSftpDetails() {
        return sftp_details;
    }

    public UserFeatureLimits getFeatureLimits() {
        return feature_limits;
    }
}
