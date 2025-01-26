package dev.grcq.nitrolib.spigot;

import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Serializable
@NoArgsConstructor
@AllArgsConstructor
public class NitroConfig {

    protected static final NitroConfig DEFAULT = new NitroConfig(false, false);

    @SerializeField("server-analytics")
    private boolean serverAnalytics;

    @SerializeField("debug-mode")
    private boolean debugMode;

}
