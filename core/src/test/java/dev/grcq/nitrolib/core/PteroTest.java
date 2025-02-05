package dev.grcq.nitrolib.core;

import dev.grcq.nitrolib.core.wrappers.pterodactyl.PteroAdmin;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.PteroUser;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.admin.server.Server;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.ServerState;
import dev.grcq.nitrolib.core.wrappers.pterodactyl.user.server.UserServer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PteroTest {

    private final PteroAdmin admin = new PteroAdmin("", "");
    private final PteroUser user = new PteroUser("", "zh02u7s1IYqFmHbe3LbebmSEsBnA6HzV");

    @Test
    public void testServers() {
        List<Server> servers = admin.getServers();
        assertNotNull(servers);
        assertFalse(servers.isEmpty());

        Server first = servers.get(0);
        assertEquals("Bot", first.getName());
        assertEquals(7, first.getId());
    }

    @Test
    public void testServer() {
        Server server = admin.getServer(7);
        assertNotNull(server);
        assertEquals("Bot", server.getName());
    }

    @Test
    public void testUserServers() {
        List<UserServer> servers = user.getServers();
        System.out.println(servers);
        assertNotNull(servers);
        assertFalse(servers.isEmpty());

        UserServer first = servers.get(0);
        assertEquals("Akero Test", first.getName());
        assertEquals(ServerState.STOPPED, user.getServerState(first));
    }
}
