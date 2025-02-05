package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class UserFeatureLimits {

    private final int databases;
    private final int allocations;
    private final int backups;

}
