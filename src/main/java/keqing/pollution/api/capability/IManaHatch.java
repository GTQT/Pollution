package keqing.pollution.api.capability;

public interface IManaHatch {
    int getMaxMana();
    int getMana();
    int getTier();
    boolean consumeMana(int amount,boolean simulate);

    double getTimeReduce();
    double getEnergyReduce();
    int getOverclockingEnhance();
    int getParallelEnhance();

}
