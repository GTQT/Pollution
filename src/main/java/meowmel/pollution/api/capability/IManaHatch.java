package meowmel.pollution.api.capability;

public interface IManaHatch {
    long getMaxMana();

    long getMana();

    boolean isFull();

    int getTier();

    void receiveMana(long mana);

    default void receiveManaFromBurst(int mana) {
        receiveMana(mana);
    }

    default boolean canReceiveManaFromBursts() {
        return !isFull();
    }

    boolean consumeMana(long amount, boolean simulate);
}
