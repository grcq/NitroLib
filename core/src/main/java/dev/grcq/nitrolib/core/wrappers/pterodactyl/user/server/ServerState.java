package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ServerState {

    INSTALLING("installing"),
    SUSPENDED("suspended"),
    INSTALL_FAILED("install_failed"),
    RUNNING("started"),
    STARTING("starting"),
    STOPPING("stopping"),
    STOPPED("offline"),
    RESTARTING("restarting"),
    KILLING("killing"),
    UNKNOWN("unknown");

    private final String identifier;

    public static ServerState fromIdentifier(String identifier) {
        for (ServerState state : values()) {
            if (state.getIdentifier().equals(identifier)) {
                return state;
            }
        }
        return UNKNOWN;
    }
}
