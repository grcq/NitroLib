package dev.grcq.nitrolib.discord.test;

import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import lombok.Data;

@Data
@Serializable
public class TestConfig {

    @SerializeField("token")
    private String token = "";

}
