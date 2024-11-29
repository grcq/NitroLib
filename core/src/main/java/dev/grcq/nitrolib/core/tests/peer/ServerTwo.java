package dev.grcq.nitrolib.core.tests.peer;

import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.peer.IPacket;
import dev.grcq.nitrolib.core.peer.Peer;

class ServerTwo {

    public static void main(String[] args) throws Exception {
        Peer peer = new Peer(6869);
        peer.start();
        System.out.println("Server started on port 6869");

        peer.send("localhost", 6868, new IPacket() {
            @Override
            public String getPacketId() {
                return "test";
            }

            @Override
            public JsonObject getPayload() {
                JsonObject payload = new JsonObject();
                payload.addProperty("message", "Hello, world!");
                return payload;
            }
        });

        JsonObject response = peer.send("localhost", 6868, new IPacket() {
            @Override
            public String getPacketId() {
                return "test2";
            }

            @Override
            public JsonObject getPayload() {
                JsonObject payload = new JsonObject();
                payload.addProperty("message", "Hello, a!");
                return payload;
            }
        });
        System.out.println("Received response: " + response);
    }

}
