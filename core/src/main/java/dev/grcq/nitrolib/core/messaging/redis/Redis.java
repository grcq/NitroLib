package dev.grcq.nitrolib.core.messaging.redis;

import dev.grcq.nitrolib.core.messaging.IPacket;
import dev.grcq.nitrolib.core.messaging.MessagingClient;
import dev.grcq.nitrolib.core.messaging.PacketListener;
import dev.grcq.nitrolib.core.utils.URIHelper;

import java.util.function.Consumer;

public class Redis implements MessagingClient {

    private final URIHelper uri;

    public Redis(String uri) {
        this.uri = new URIHelper(uri);
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(IPacket packet) {

    }

    @Override
    public void sendTo(String queue, IPacket packet) {

    }

    @Override
    public void sendAsync(IPacket packet) {

    }

    @Override
    public void sendToAsync(String queue, IPacket packet) {

    }

    @Override
    public IPacket get(IPacket packet) {
        return null;
    }

    @Override
    public IPacket getFrom(String queue, IPacket packet) {
        return null;
    }

    @Override
    public void get(IPacket packet, Consumer<IPacket> callback) {

    }

    @Override
    public void getFrom(String queue, IPacket packet, Consumer<IPacket> callback) {

    }

    @Override
    public void register(PacketListener listener) {

    }

    @Override
    public void unregister(PacketListener listener) {

    }

    @Override
    public void listen() {

    }
}
