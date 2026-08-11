package meowmel.pollution.api.capability;

/** Shared limits for the directed constellation-energy network. */
public final class StarstreamNetworkConstants {

    public static final int DIRECT_LINK_RANGE = 256;
    public static final int DIRECT_LINK_RANGE_SQUARED = DIRECT_LINK_RANGE * DIRECT_LINK_RANGE;
    public static final int MAX_DIRECT_INPUT_LINKS = 64;
    public static final int MAX_RELAY_INPUTS = 16;
    public static final int MAX_RELAY_HOPS = 16;
    public static final long RELAY_TRANSFER_PER_TICK = 32_768L;
    public static final long RELAY_WIRELESS_OUTPUT_PER_TICK = 32_768L;
    public static final int NEXUS_WIRELESS_RANGE = 128;
    public static final int NEXUS_WIRELESS_RANGE_SQUARED =
            NEXUS_WIRELESS_RANGE * NEXUS_WIRELESS_RANGE;
    public static final int RELAY_WIRELESS_RANGE = 64;
    public static final int RELAY_WIRELESS_RANGE_SQUARED =
            RELAY_WIRELESS_RANGE * RELAY_WIRELESS_RANGE;
    public static final long NEXUS_WIRELESS_OUTPUT_PER_TICK = 524_288L;
    public static final int INTERDIMENSIONAL_RELAY_RANGE = 96;
    public static final int INTERDIMENSIONAL_RELAY_RANGE_SQUARED =
            INTERDIMENSIONAL_RELAY_RANGE * INTERDIMENSIONAL_RELAY_RANGE;
    public static final long INTERDIMENSIONAL_RELAY_OUTPUT_PER_TICK = 65_536L;
    public static final long DEFAULT_CONSUMER_OUTPUT_PER_TICK = 8_192L;
    public static final int MAX_REGISTERED_RELAYS = 1_024;
    public static final int MAX_REGISTERED_TERMINALS = 1_024;
    public static final int RELAY_HEARTBEAT_INTERVAL = 20;
    public static final int RELAY_OFFLINE_TIMEOUT = 100;
    public static final int TERMINAL_OFFLINE_TIMEOUT = 100;
    public static final int STALE_RECORD_TIMEOUT = 12_000;

    private StarstreamNetworkConstants() {}
}
