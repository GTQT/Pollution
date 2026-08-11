package meowmel.pollution.api.capability;

/** Runtime state reported by a reusable wireless terminal binding. */
public enum StarstreamWirelessTerminalStatus {

    UNBOUND("unbound"),
    DISCOVERING("discovering"),
    ONLINE("online"),
    NETWORK_OFFLINE("network_offline"),
    PROVIDER_UNAVAILABLE("provider_unavailable"),
    WAITING_FOR_ENERGY("waiting_for_energy"),
    INVALID_CHANNEL("invalid_channel"),
    INVALID_REQUEST("invalid_request"),
    WRONG_SIDE("wrong_side");

    private final String key;

    StarstreamWirelessTerminalStatus(String key) {
        this.key = key;
    }

    public String getTranslationKey() {
        return "pollution.starstream_terminal.status." + key;
    }
}
