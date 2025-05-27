package dev.grcq.nitrolib.discord;

import com.google.common.base.Preconditions;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public abstract class AbstractBot implements Bot {

    @Getter
    private JDA jda;

    abstract public void onStart();
    abstract public void onStop();

    @Override
    public final void start(String token) {
        Preconditions.checkState(this.jda == null, "JDA instance already created");
        Preconditions.checkArgument(token != null && !token.isEmpty(), "Token cannot be null or empty");

        LogUtil.info("Building JDA instance...");
        this.jda = JDABuilder
                .create("token", getIntents())
                .build();

        LogUtil.info("JDA instance built, logged in as %s", jda.getSelfUser().getAsTag());

        Thread shutdownHook = new Thread(() -> {
            LogUtil.info("Stop signal received.");
            this.jda.shutdown();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        onStart();
    }

    @Override
    public final void stop() {
        Preconditions.checkState(this.jda != null, "No JDA instance running");
        LogUtil.info("Shutting down JDA instance...");
        this.jda.shutdown();
        this.jda = null;
        onStop();
    }

    @Override
    public void setActivity(Activity activity) {
        this.jda.getPresence().setActivity(activity);
    }
}
