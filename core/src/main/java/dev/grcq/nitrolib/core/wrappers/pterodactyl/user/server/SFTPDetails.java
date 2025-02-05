package dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class SFTPDetails {

    private final String ip;
    private final int port;

}
