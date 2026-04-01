package meowmel.pollution.api.capability;

public interface IManaHatch {
    long getMaxMana();

    long getMana();

    int getAmp();

    boolean isFull();

    int getTier();

    void receiveMana(int mana);

    boolean consumeMana(long amount, boolean simulate);
}
