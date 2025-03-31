package dev.grcq.nitrolib.core.messaging;

import com.google.gson.JsonElement;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    default void handlePacket(PacketListener listener, IPacket packet) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Packet.class)) continue;

            Packet annotation = method.getAnnotation(Packet.class);
            if (!annotation.value().equals(packet.getIdentifier())) continue;

            if (method.getParameterCount() != 1) {
                LogUtil.warn("[RabbitMQ] Method %s is annotated with @Packet and has %d parameters.", method.getName());
                LogUtil.warn("[RabbitMQ] If this is not intentional, please add one parameter of IPacket or any subtype of JsonElement", method.getName());
                continue;
            }

            Class<?> parameter = method.getParameterTypes()[0];
            if (parameter != IPacket.class && !JsonElement.class.isAssignableFrom(parameter)) {
                LogUtil.warn("[RabbitMQ] Method %s is annotated with @Packet and has a parameter that is not IPacket or any subtype of JsonElement", method.getName());
                continue;
            }

            try {
                method.setAccessible(true);
                Object instance = Modifier.isStatic(method.getModifiers()) ? null : listener;
                if (parameter == IPacket.class) {
                    method.invoke(instance, packet);
                } else {
                    method.invoke(instance, packet.getPayload());
                }
            } catch (Exception e) {
                LogUtil.handleException("[RabbitMQ] Failed to handle packet", e);
            }
        }
    }
}
