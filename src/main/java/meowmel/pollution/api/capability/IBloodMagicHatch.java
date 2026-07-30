package meowmel.pollution.api.capability;

/** A Blood Magic orb backed source of life essence for a multiblock. */
public interface IBloodMagicHatch {

    int getLifeEssence();

    int getLifeEssenceCapacity();

    boolean consumeLifeEssence(int amount, boolean simulate);
}
