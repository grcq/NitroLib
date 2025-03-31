package dev.grcq.nitrolib.core.messaging.redis;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.messaging.IPacket;
import dev.grcq.nitrolib.core.messaging.MessagingClient;
import dev.grcq.nitrolib.core.messaging.PacketListener;
import dev.grcq.nitrolib.core.utils.URIHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Redis implements MessagingClient {

    private final URIHelper uri;

    private Jedis jedis;
    private JedisPubSub pubSub;

    private final String channel;
    private final List<String> channels;
    private final List<PacketListener> listeners;

    public Redis(String uri, String channel) {
        this.uri = new URIHelper(uri);
        this.channel = channel;
        this.channels = Lists.newArrayList(channel);
        this.listeners = Lists.newArrayList();
    }

    @Override
    public void connect() {
        URI redisUri = this.uri.toURI();
        this.jedis = new Jedis(redisUri);
        this.listen();
    }

    @Override
    public void disconnect() {
        Preconditions.checkNotNull(this.jedis, "Jedis is not connected");

        this.pubSub.unsubscribe();
        this.jedis.close();

        this.jedis = null;
        this.pubSub = null;
    }

    @Override
    public void send(IPacket packet) {

    }

    @Override
    public void sendTo(String channel, IPacket packet) {

    }

    @Override
    public void sendAsync(IPacket packet) {
        CompletableFuture.runAsync(() -> {
            send(packet);
        });
    }

    @Override
    public void sendToAsync(String channel, IPacket packet) {
        CompletableFuture.runAsync(() -> {
            sendTo(channel, packet);
        });
    }

    @Override
    public IPacket get(IPacket packet) {
        return null;
    }

    @Override
    public IPacket getFrom(String channel, IPacket packet) {
        return null;
    }

    @Override
    public void get(IPacket packet, Consumer<IPacket> callback) {
        CompletableFuture.runAsync(() -> {
            IPacket response = get(packet);
            callback.accept(response);
        });
    }

    @Override
    public void getFrom(String channel, IPacket packet, Consumer<IPacket> callback) {
        CompletableFuture.runAsync(() -> {
            IPacket response = getFrom(channel, packet);
            callback.accept(response);
        });
    }

    @Override
    public void register(PacketListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregister(PacketListener listener) {
        this.listeners.remove(listener);
    }

    public Redis subscribe(String... channels) {
        this.channels.addAll(Lists.newArrayList(channels));
        return this;
    }

    private void listen() {
        Preconditions.checkNotNull(this.jedis, "Jedis is not connected");

        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (!channel.equals(Redis.this.channel)) return;

                IPacket packet = IPacket.deserialize(message);
                for (PacketListener listener : Redis.this.listeners) {
                    handlePacket(listener, packet);
                }
            }
        };
        new Thread(() -> this.jedis.subscribe(this.pubSub, this.channels.toArray(new String[0]))).start();
    }


}
