package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PowerAction {

    START("start"),
    STOP("stop"),
    RESTART("restart"),
    KILL("kill");

    private final String action;

}
