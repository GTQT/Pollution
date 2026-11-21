package meowmel.pollution.api.unification.materials;

import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;

public class MaterialFlagAddition {
    public static void init() {
        // The fusion reactor infusion still requires blockUranium235.
        Materials.Uranium235.addFlags(MaterialFlags.FORCE_GENERATE_BLOCK);
    }
}
