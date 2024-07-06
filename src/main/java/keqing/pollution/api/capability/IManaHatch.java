package keqing.pollution.api.capability;

public interface IManaHatch {
    int getMaxMana();
    int getMana();
    int getTier();
    boolean consumeMana(int amount);
}
