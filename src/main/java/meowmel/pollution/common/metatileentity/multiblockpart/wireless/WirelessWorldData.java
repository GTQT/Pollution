package meowmel.pollution.common.metatileentity.multiblockpart.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessWorldData extends WorldSavedData {
    private static final String DATA_NAME = "WirelessEnergyData";

    private final Map<Integer, Long> data = new HashMap<>();

    public WirelessWorldData() {
        super(DATA_NAME);
    }

    public WirelessWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        data.clear();
        NBTTagCompound dims = nbt.getCompoundTag("DimData");
        for (String key : dims.getKeySet()) {
            int dim = Integer.parseInt(key);
            long energy = dims.getLong(key);
            data.put(dim, energy);
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound nbt) {
        NBTTagCompound dims = new NBTTagCompound();
        for (Map.Entry<Integer, Long> entry : data.entrySet()) {
            dims.setLong(entry.getKey().toString(), entry.getValue());
        }
        nbt.setTag("DimData", dims);
        return nbt;
    }

    public Map<Integer, Long> getData() {
        return data;
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