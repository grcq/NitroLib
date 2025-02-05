package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@ToString
@AllArgsConstructor
public class UserAllocation {

    @Getter private final int id;
    @Getter private final String ip;
    @Getter private final int port;

    @Nullable private final String ip_alias;
    @Getter @Nullable private final String notes;
    private final boolean is_default;

    public String getIpAlias() {
        return ip_alias;
    }

    public boolean isDefault() {
        return is_default;
    }
}
