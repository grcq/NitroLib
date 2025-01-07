package dev.grcq.nitrolib.core.serialization.test;

import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@Serializable
public class TestClass {

    @SerializeField("id")
    private String id;

    @SerializeField("display-name")
    private String displayName;

    @SerializeField("glow")
    private boolean glow;

    @SerializeField("child")
    private TestClass child;

    @SerializeField("list")
    private List<String> list;

}
