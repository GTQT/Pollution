package meowmel.pollution.common.metatileentity.multiblockpart.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WirelessWorldData extends WorldSavedData {
    private static final String DATA_NAME = "WirelessEnergyData";
    private static final String LEGACY_DIM_DATA = "DimData";
    private static final String ENERGY_MANA_DIM_DATA = "EnergyManaDimData";
    private static final String MANA_POOL_DIM_DATA = "ManaPoolDimData";

    private final Map<Integer, Long> energyManaData = new HashMap<>();
    private final Map<Integer, Long> manaPoolData = new HashMap<>();

    public WirelessWorldData() {
        super(DATA_NAME);
    }

    public WirelessWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        energyManaData.clear();
        manaPoolData.clear();

        // Legacy worlds used one shared DimData map. Migrate that balance to
        // energy-type mana only so the split cannot duplicate stored mana.
        String energyKey = nbt.hasKey(ENERGY_MANA_DIM_DATA, 10)
                ? ENERGY_MANA_DIM_DATA : LEGACY_DIM_DATA;
        readDimensionMap(nbt.getCompoundTag(energyKey), energyManaData);
        readDimensionMap(nbt.getCompoundTag(MANA_POOL_DIM_DATA), manaPoolData);
    }

    private static void readDimensionMap(NBTTagCompound dims, Map<Integer, Long> target) {
        for (String key : dims.getKeySet()) {
            int dim = Integer.parseInt(key);
            long amount = dims.getLong(key);
            if (amount > 0L) {
                target.put(dim, amount);
            }
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound nbt) {
        nbt.removeTag(LEGACY_DIM_DATA);
        nbt.setTag(ENERGY_MANA_DIM_DATA, writeDimensionMap(energyManaData));
        nbt.setTag(MANA_POOL_DIM_DATA, writeDimensionMap(manaPoolData));
        return nbt;
    }

    private static NBTTagCompound writeDimensionMap(Map<Integer, Long> source) {
        NBTTagCompound dims = new NBTTagCompound();
        for (Map.Entry<Integer, Long> entry : source.entrySet()) {
            dims.setLong(entry.getKey().toString(), entry.getValue());
        }
        return dims;
    }

    public Map<Integer, Long> getEnergyManaData() {
        return energyManaData;
    }

    public Map<Integer, Long> getManaPoolData() {
        return manaPoolData;
    }

    public static WirelessWorldData get(World world) {
        WirelessWorldData instance = (WirelessWorldData) world.loadData(WirelessWorldData.class, DATA_NAME);
        if (instance == null) {
            instance = new WirelessWorldData();
            world.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
