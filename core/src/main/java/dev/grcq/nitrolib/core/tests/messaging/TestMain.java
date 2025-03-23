package dev.grcq.nitrolib.core.tests.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.messaging.IPacket;
import dev.grcq.nitrolib.core.messaging.Packet;
import dev.grcq.nitrolib.core.messaging.PacketListener;
import dev.grcq.nitrolib.core.messaging.rabbitmq.RabbitMQ;

import java.util.Scanner;

public class TestMain {

    public static void main(String[] args) {
        RabbitMQ rabbitMQ = new RabbitMQ("192.168.0.75", 5672, "test", "test", "/", "test");
        System.out.println("Connecting to RabbitMQ...");
        rabbitMQ.connect();
        System.out.println("Connected to RabbitMQ!");
        rabbitMQ.register(new PacketListener() {
            @Packet("test")
            public void onTestPacket(JsonElement packet) {
                System.out.println(packet);
            }
        });

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("exit")) {
                break;
            }
        }

        rabbitMQ.disconnect();
    }

}
