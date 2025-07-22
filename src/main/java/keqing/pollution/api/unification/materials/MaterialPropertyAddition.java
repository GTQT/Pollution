package keqing.pollution.api.unification.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.FluidProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.stack.MaterialStack;

import static gregtech.api.unification.material.Materials.*;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class MaterialPropertyAddition {
    public static void addMaterialProperties() {
        //用于混合core的材料
        Okin.setComponents(new MaterialStack(energy_crystal,4),new MaterialStack(infused_entropy,1),new MaterialStack(infused_order,1));

        Octahedrite.setComponents(new MaterialStack(Okin,6),new MaterialStack(TitanSteel,6),new MaterialStack(BlackSteel,2),new MaterialStack(thaumium,4),new MaterialStack(energy_crystal,1));

        var blastProperty = new BlastProperty();
        blastProperty.setBlastTemperature(8000);
        blastProperty.setGasTier(BlastProperty.GasTier.HIGH);
        Octahedrite.setProperty(PropertyKey.INGOT, blastProperty);
    }
}
