package dev.grcq.nitrolib.core.messaging;

import java.util.function.Consumer;

public interface MessagingClient {

    void connect();
    void disconnect();

    void send(IPacket packet);
    void sendTo(String queue, IPacket packet);
    void sendAsync(IPacket packet);
    void sendToAsync(String queue, IPacket packet);
    IPacket get(IPacket packet);
    IPacket getFrom(String queue, IPacket packet);
    void get(IPacket packet, Consumer<IPacket> callback);
    void getFrom(String queue, IPacket packet, Consumer<IPacket> callback);

    void register(PacketListener listener);
    void unregister(PacketListener listener);

    void listen() throws Exception;
}
