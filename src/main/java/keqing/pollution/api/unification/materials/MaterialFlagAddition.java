package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;

import static gregtech.api.unification.material.Materials.*;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class MaterialFlagAddition {
    public static void init() {
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
