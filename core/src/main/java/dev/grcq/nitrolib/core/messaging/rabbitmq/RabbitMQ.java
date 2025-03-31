package dev.grcq.nitrolib.core.messaging.rabbitmq;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dev.grcq.nitrolib.core.messaging.IPacket;
import dev.grcq.nitrolib.core.messaging.MessagingClient;
import dev.grcq.nitrolib.core.messaging.Packet;
import dev.grcq.nitrolib.core.messaging.PacketListener;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Protocol;
import dev.grcq.nitrolib.core.utils.URIHelper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class RabbitMQ implements MessagingClient {

    @Getter
    private final URIHelper uri;
    @Getter
    private String exchange;

    private final ConcurrentHashMap<String, BlockingQueue<IPacket>> responseMap = new ConcurrentHashMap<>();
    private final List<PacketListener> listeners = new ArrayList<>();

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private Thread listener;

    public RabbitMQ(String uri, String exchange) {
        this.uri = new URIHelper(uri);
        this.exchange = exchange;
    }

    public RabbitMQ(String host, int port, String exchange) {
        this(host, port, "guest", "guest", exchange);
    }

    public RabbitMQ(String host, int port, String username, String password, String exchange) {
        this(host, port, username, password, "/", exchange);
    }

    public RabbitMQ(String host, int port, String username, String password, String vhost, String exchange) {
        this.uri = new URIHelper(Protocol.AMQP, host, port, username, password, vhost);
        this.exchange = exchange;
    }

    public RabbitMQ(String host, String username, String password, String vhost, String exchange) {
        this(host, 5672, username, password, vhost, exchange);
    }

    public RabbitMQ(String host, String username, String password, String exchange) {
        this(host, 5672, username, password, exchange);
    }

    @Override
    public void connect() {
        Preconditions.checkState(this.factory == null && this.connection == null, "[RabbitMQ] Already connected to RabbitMQ");

        this.factory = new ConnectionFactory();
        this.factory.setHost(this.uri.getHost());
        this.factory.setPort(this.uri.getPort());
        this.factory.setUsername(this.uri.getUsername());
        this.factory.setPassword(this.uri.getPassword());
        this.factory.setVirtualHost(this.uri.getPathname());
        try {
            this.connection = this.factory.newConnection();
            this.channel = this.connection.createChannel();
            this.listen();
        } catch (Exception e) {
            LogUtil.handleException("[RabbitMQ] Failed to connect to RabbitMQ", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (this.channel != null) this.channel.close();
            if (this.connection != null) this.connection.close();
            if (this.listener != null) this.listener.interrupt();
        } catch (Exception e) {
            LogUtil.handleException("[RabbitMQ] Failed to disconnect from RabbitMQ", e);
        } finally {
            this.factory = null;
            this.connection = null;
            this.channel = null;
            this.listener = null;
        }
    }

    /**
     * Send a packet to RabbitMQ
     * <p>
     *     This method will send a packet to RabbitMQ
     *     The packet will be sent to the exchange
     *
     * </p>
     * Example:
     * <pre>
     *     IPacket packet = ...;
     *     rabbitMQ.send(packet);
     *     // ...
     * </pre>
     * @param packet
     */
    @Override
    public void send(@NotNull IPacket packet) {
        sendTo(this.exchange, packet);
    }

    @Override
    public void sendTo(String queue, IPacket packet) {
        Preconditions.checkNotNull(this.channel, "[RabbitMQ] Channel is not connected");
        Preconditions.checkNotNull(packet, "[RabbitMQ] Packet cannot be null");

        try {
            JsonObject object = new JsonObject();
            object.addProperty("identifier", packet.getIdentifier());
            object.add("payload", packet.getPayload());

            this.channel.basicPublish("", queue, null, object.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LogUtil.handleException("[RabbitMQ] Failed to send packet to RabbitMQ", e);
        }
    }

    /**
     * Send a packet to RabbitMQ asynchronously
     * <p>
     *     This method will send a packet to RabbitMQ asynchronously
     *     The packet will be sent to the exchange
     * </p>
     * Example:
     * <pre>
     *     IPacket packet = ...;
     *     rabbitMQ.sendAsync(packet);
     *     // ...
     * </pre>
     * @param packet
     */
    @Override
    public void sendAsync(IPacket packet) {
        CompletableFuture.runAsync(() -> send(packet));
    }

    @Override
    public void sendToAsync(String queue, IPacket packet) {
        CompletableFuture.runAsync(() -> sendTo(queue, packet));
    }

    /**
     * Get a packet from RabbitMQ
     * <p>
     *     This method will send a packet to RabbitMQ and wait for a response packet
     *     The response packet will be returned
     *     If the response packet is not received within 5 seconds, it will return null
     * </p>
     * Example:
     * <pre>
     *     IPacket packet = ...;
     *     IPacket response = rabbitMQ.get(packet);
     *     if (response != null) {
     *         String identifier = response.getIdentifier();
     *         JsonObject payload = response.getPayload();
     *         // ...
     *     }
     * </pre>
     * @param packet The packet to send
     * @return The response packet where `identifier` is the identifier of the packet and `payload` is the payload of the packet
     */
    @Override
    public IPacket get(@NotNull IPacket packet) {
        return getFrom(this.exchange, packet);
    }

    @Override
    public IPacket getFrom(String queue, IPacket packet) {
        Preconditions.checkNotNull(this.channel, "[RabbitMQ] Channel is not connected");
        Preconditions.checkNotNull(packet, "[RabbitMQ] Packet cannot be null");

        String correlationId = UUID.randomUUID().toString();
        final BlockingQueue<IPacket> response = new ArrayBlockingQueue<>(1);
        this.responseMap.put(correlationId, response);
        try {
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId)
                    .replyTo(this.exchange)
                    .build();

            JsonObject object = new JsonObject();
            object.addProperty("identifier", packet.getIdentifier());
            object.add("payload", packet.getPayload());

            this.channel.basicPublish("", queue, properties, object.toString().getBytes(StandardCharsets.UTF_8));
            IPacket responsePacket = response.poll(5, TimeUnit.SECONDS);
            if (responsePacket == null) LogUtil.warn("[RabbitMQ] Failed to get response packet for %s", packet.getIdentifier());

            return responsePacket;
        } catch (Exception e) {
            LogUtil.handleException("[RabbitMQ] Failed to get packet from RabbitMQ", e);
            return null;
        } finally {
            this.responseMap.remove(correlationId);
        }
    }

    @Override
    public void get(IPacket packet, Consumer<IPacket> callback) {
        CompletableFuture.runAsync(() -> {
            IPacket response = get(packet);
            callback.accept(response);
        });
    }

    @Override
    public void getFrom(String queue, IPacket packet, Consumer<IPacket> callback) {
        CompletableFuture.runAsync(() -> {
            IPacket response = getFrom(queue, packet);
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

    private void listen() throws Exception {
        Preconditions.checkNotNull(this.channel, "[RabbitMQ] Channel is not connected");
        Preconditions.checkState(this.listener == null || !this.listener.isAlive(), "[RabbitMQ] Listener is already running");

        this.channel.queueDeclare(this.exchange, false, false, false, null);
        this.listener = new Thread(() -> {
            try {
                this.channel.basicConsume(this.exchange, true, (consumerTag, delivery) -> {
                    IPacket packet = null;
                    try {
                        packet = IPacket.deserialize(new String(delivery.getBody(), StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        LogUtil.handleException("[RabbitMQ] Failed to deserialize packet", e);
                    }

                    if (packet != null) {
                        String correlationId = delivery.getProperties().getCorrelationId();
                        if (correlationId != null && responseMap.containsKey(correlationId)) {
                            BlockingQueue<IPacket> response = this.responseMap.get(correlationId);
                            if (response != null && !response.offer(packet)) {
                                LogUtil.warn("[RabbitMQ] Failed to offer response packet for %s", packet.getIdentifier());
                            }
                        } if (correlationId != null) {
                            // respond

                        } else {
                            for (PacketListener listener : this.listeners) {
                                handlePacket(listener, packet);
                            }
                        }
                    }
                }, consumerTag -> {});
            } catch (Exception e) {
                LogUtil.handleException("[RabbitMQ] Failed to listen for packets", e);
            }
        });
        this.listener.start();
    }
}
