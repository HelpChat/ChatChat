package at.helpch.chatchat.util;

public final class Constants {
    public static final String CROSS_SERVER_SUB_CHANNEL = "hc:cc";
    public static final String BUNGEE_CROSS_SERVER_CHANNEL = "BungeeCord";
    public static final String PUBLIC_MESSAGE_TYPE = "pub";
    public static final String PRIVATE_MESSAGE_TYPE = "prv";
    public static final String PRIVATE_MESSAGE_ACK_TYPE = "prv_ack";
    public static final long PRIVATE_MESSAGE_ACK_TIMEOUT_TICKS = 40L;

    private Constants() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }
}
