package dev.grcq.nitrolib.core.peer;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface PacketListenRunnable {
    JsonObject run(IPacket packet);
}
