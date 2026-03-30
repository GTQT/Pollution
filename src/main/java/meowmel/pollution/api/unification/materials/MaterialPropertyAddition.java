package meowmel.pollution.api.unification.materials;

import gregtech.api.unification.material.properties.DustProperty;
import gregtech.api.unification.material.properties.MaterialToolProperty;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;

import static gregtech.api.unification.material.Materials.*;
import static meowmel.pollution.api.unification.PollutionMaterials.*;

public class MaterialPropertyAddition {
    public static void init() {
        /*
        //用于混合core的材料
        Okin.setComponents(new MaterialStack(energy_crystal,4),new MaterialStack(infused_entropy,1),new MaterialStack(infused_order,1));

        Octahedrite.setComponents(new MaterialStack(Okin,6),new MaterialStack(TitanSteel,6),new MaterialStack(BlackSteel,2),new MaterialStack(thaumium,4),new MaterialStack(energy_crystal,1));

        var blastProperty = new BlastProperty();
        blastProperty.setBlastTemperature(8000);
        blastProperty.setGasTier(BlastProperty.GasTier.HIGH);
        Octahedrite.setProperty(PropertyKey.INGOT, blastProperty);

         */

        GTQTMaterials.Thaumium.setProperty(PropertyKey.DUST, new DustProperty());
        GTQTMaterials.Thaumium.setProperty(PropertyKey.TOOL, new MaterialToolProperty(5, 4, 1024, 3));

        OreProperty oreProp = Pyrargyrite.getProperty(PropertyKey.ORE);
        oreProp.setOreByProducts(Silver, Sulfur, Antimony);
        oreProp.setWashedIn(SodiumPersulfate);
        oreProp.setDirectSmeltResult(Silver);

        oreProp = PlutoZinc.getProperty(PropertyKey.ORE);
        oreProp.setOreByProducts(Silver, Sulfur, Tin);
        oreProp.setWashedIn(SodiumPersulfate);
        oreProp.setDirectSmeltResult(Zinc);

        oreProp = AuthorityLead.getProperty(PropertyKey.ORE);
        oreProp.setOreByProducts(Silver, Lead, Iron);
        oreProp.setWashedIn(Water);
        oreProp.setDirectSmeltResult(Lead);

        oreProp = MeltGold.getProperty(PropertyKey.ORE);
        oreProp.setOreByProducts(Gold, Silver, Lead);
        oreProp.setWashedIn(Water);
        oreProp.setDirectSmeltResult(Gold);
    }
}
