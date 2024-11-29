package dev.grcq.nitrolib.core.tests.peer;

import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.peer.Peer;

class ServerOne {

    public static void main(String[] args) throws Exception {
        Peer peer = new Peer(6868).listen("test", packet -> {
            System.out.println("Received packet: " + packet.getPayload());
            return null;
        }).listen("test2", packet -> {
            System.out.println("Received packet 2: " + packet.getPayload());
            JsonObject response = new JsonObject();
            response.addProperty("message", "Hello, client!");
            return response;
        });
        peer.start();
        System.out.println("Server started on port 6868");
    }

}
