package dev.grcq.nitrolib.core.peer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.utils.Util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Peer {

    private final ServerSocket server;
    private final Map<String, PacketListenRunnable> listeners;

    public Peer(String host, int port) throws Exception {
        this.server = new ServerSocket();
        this.server.bind(new InetSocketAddress(host, port));

        this.listeners = new HashMap<>();
    }

    public Peer(int port) throws Exception {
        this("localhost", port);
    }

    public void close() throws Exception {
        this.server.close();
    }

    public Peer listen(String packetId, PacketListenRunnable runnable) {
        this.listeners.put(packetId, runnable);
        return this;
    }

    public JsonObject send(String host, int port, IPacket packet) {
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
            Util.handleException("Failed to send packet", e);
            return null;
        }
    }

    public void start() {
        new Thread(() -> {
            while (true) {
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
