package dev.grcq.nitrolib.core.wrappers.pterodactyl.admin.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ServerContainer {

    private final String startup_command;
    @Getter private final String image;
    @Getter private final boolean installed;
    @Getter private final Map<String, String> environment;

    private final Date created_at;
    private final Date updated_at;

    public String getStartupCommand() {
        return startup_command;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public Date getCreatedAt() {
        return created_at;
    }
}
