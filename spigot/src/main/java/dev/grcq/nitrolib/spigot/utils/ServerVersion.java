package dev.grcq.nitrolib.spigot.utils;

import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerVersion {

    V1_7(1),
    V1_8(2),
    V1_9(3),
    V1_10(4),
    V1_11(5),
    V1_12(6),
    V1_13(7),
    V1_14(8),
    V1_15(9),
    V1_16(10),
    V1_17(11),
    V1_18(12),
    V1_19(13),
    V1_20(14),
    V1_21(15),
    NOT_SUPPORTED(-1);

    @Getter
    private static final ServerVersion currentVersion;

    static {
        String nmsVersion = NMSUtil.getNMSVersion();
        String[] split = nmsVersion.split("_");
        ServerVersion current;
        try {
            current = ServerVersion.valueOf(split[0].toUpperCase() + "_" + split[1]);
        } catch (IllegalArgumentException e) {
            current = NOT_SUPPORTED;
            LogUtil.error("Detected an unsupported version for NitroLib from your server: " + nmsVersion);
            LogUtil.error("Either update NitroLib or downgrade your server to a supported version.");
        }

        currentVersion = current;
    }

    private final int version;

    public boolean higher(ServerVersion version) {
        return this.version > version.version;
    }

    public boolean lower(ServerVersion version) {
        return this.version < version.version;
    }
}
