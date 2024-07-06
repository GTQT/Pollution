package keqing.pollution.api.capability;

public interface  IManaMultiblock {
    default boolean isMana() {
        return true;
    }

    default int getTier() {
        return 1;
    }

    default boolean work()
    {
        return false;
    }
}
