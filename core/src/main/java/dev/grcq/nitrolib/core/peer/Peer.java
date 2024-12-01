package dev.grcq.nitrolib.core.peer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Peer {

    private boolean running = false;
    @Setter @Getter private String host;
    @Setter @Getter private int port;

    private ServerSocket server;
    private final Map<String, PacketListenRunnable> listeners;

    public Peer(String host, int port) {
        this.host = host;
        this.port = port;
        this.listeners = new HashMap<>();
    }

    public Peer(int port) {
        this("localhost", port);
    }

    /**
     * Close the peer server
     * @throws Exception If the server could not be closed
     */
    public void close() throws Exception {
        this.running = false;
        this.server.close();
        this.server = null;
    }

    /**
     * Listen for a packet
     * @param packetId The ID of the packet to listen for
     * @param runnable The runnable to run when the packet is received
     * @return The peer instance
     */
    public Peer listen(@NotNull String packetId, @NotNull PacketListenRunnable runnable) {
        this.listeners.put(packetId, runnable);
        return this;
    }

    /**
     * Clear all listeners
     */
    public void clearListeners() {
        this.listeners.clear();
    }

    /**
     * Send a packet to a host
     * @param host The IP address to send the packet to
     * @param port The port to send the packet to
     * @param packet The packet to send
     * @return The response from the host, or null if there was no response
     */
    @Nullable
    public JsonObject send(@NotNull String host, int port, @NotNull IPacket packet) {
        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream);
            out.println(packet.getPacketId());
            out.println(packet.getPayload().toString());
            out.flush();

            InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String response = in.readLine();
            return response.equals(Constants.NO_RESPONSE) ? null : JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            LogUtil.handleException("Failed to send packet", e);
            return null;
        }
    }

    /**
     * Start the peer server in a thread
     * @throws IOException If the server could not be started
     */
    public void start() throws IOException {
        this.running = true;

        this.server = new ServerSocket();
        this.server.bind(new InetSocketAddress(host, port));

        new Thread(() -> {
            while (running) {
                try {
                    Socket socket = this.server.accept();
                    new Thread(() -> {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                            String packetId = in.readLine();
                            String payload = in.readLine();

                            PacketListenRunnable runnable = this.listeners.get(packetId);
                            if (runnable != null) {
                                IPacket packet = new IPacket() {
                                    @Override
                                    public String getPacketId() {
                                        return packetId;
                                    }

                                    @Override
                                    public JsonObject getPayload() {
                                        return JsonParser.parseString(payload).getAsJsonObject();
                                    }
                                };
                                JsonObject response = runnable.run(packet);
                                PrintWriter out = new PrintWriter(socket.getOutputStream());
                                out.println(response == null ? Constants.NO_RESPONSE : response.toString());
                                out.flush();

                                in.close();
                                out.close();
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
