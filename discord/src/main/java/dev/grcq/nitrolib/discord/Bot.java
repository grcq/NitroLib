package dev.grcq.nitrolib.discord;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Bot {

    void start(String token);
    void stop();

    default List<GatewayIntent> getIntents() {
        return Arrays.asList(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS
        );
    }

    void setActivity(Activity activity);
    default void setActivity(Activity.ActivityType type, String name) {
        setActivity(Activity.of(type, name));
    }
    default void setActivity(Activity.ActivityType type, String name, String url) {
        setActivity(Activity.of(type, name, url));
    }

}
