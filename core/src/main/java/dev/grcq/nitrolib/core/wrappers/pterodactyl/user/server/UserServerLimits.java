package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class UserServerLimits {

    private final long memory;
    private final long swap;
    private final long disk;
    private final long io;
    private final long cpu;

}
