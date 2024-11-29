package dev.grcq.nitrolib.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public interface Constants {

    Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    Gson GSON_PPT = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setPrettyPrinting().create();

    String NO_RESPONSE = "NO_RESPONSE_wy7n3iomfg83_socket";

}
