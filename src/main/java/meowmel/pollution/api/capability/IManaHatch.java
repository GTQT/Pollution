package meowmel.pollution.api.capability;

public interface IManaHatch {
    long getMaxMana();

    long getMana();

    boolean isFull();

    int getTier();

    void receiveMana(long mana);
}
