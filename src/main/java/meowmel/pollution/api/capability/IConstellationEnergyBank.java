package meowmel.pollution.api.capability;

/**
 * Direct-access storage exposed by the Starstream Nexus Obelisk core.
 * Constellation identifiers are the canonical Astral Sorcery registry names.
 */
public interface IConstellationEnergyBank {

    long getConstellationEnergyStored(String constellationId);

    long getConstellationEnergyCapacity(String constellationId);

    long getTotalConstellationEnergyStored();

    long getTotalConstellationEnergyCapacity();

    long receiveConstellationEnergy(String constellationId, long amount, boolean simulate);

    long extractConstellationEnergy(String constellationId, long amount, boolean simulate);
}
