package dev.grcq.nitrolib.core.tests.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.grcq.nitrolib.core.messaging.IPacket;
import dev.grcq.nitrolib.core.messaging.Packet;
import dev.grcq.nitrolib.core.messaging.PacketListener;
import dev.grcq.nitrolib.core.messaging.rabbitmq.RabbitMQ;
import org.junit.Test;

public class RabbitMQTest {

    static class TestPacket implements IPacket {
        String message;
        public TestPacket(String message) {
            this.message = message;
        }

        @Override
        public String getIdentifier() {
            return "test";
        }

        @Override
        public JsonElement getPayload() {
            return new JsonPrimitive(message);
        }

        @Override
        public String toString() {
            return "TestPacket{" +
                    "message='" + message + "', " +
                    "payload='" + getPayload() + "', " +
                    '}';
        }
    }

    public static void main(String[] args) {
        RabbitMQ rabbitMQ = new RabbitMQ("192.168.0.75", 5672, "test", "test", "/", "abc");
        System.out.println("Connecting to RabbitMQ...");
        rabbitMQ.connect();

        System.out.println("Sending packet...");
        rabbitMQ.sendTo("test", new TestPacket("Hello World!"));
    }

}
