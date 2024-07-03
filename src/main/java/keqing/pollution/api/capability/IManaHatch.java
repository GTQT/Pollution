package keqing.pollution.api.capability;

public interface IManaHatch {
    int getMaxMana();
    int getMana();
    boolean consumeMana(int amount);
}
