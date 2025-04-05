package keqing.pollution.mixin;

import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.recipes.RecipeMap;
import gregtech.common.metatileentities.multi.MetaTileEntityCokeOven;
import keqing.pollution.POConfig;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.aura.AuraHelper;


@Mixin(MetaTileEntityCokeOven.class)
public abstract class MixinMetaTileEntityMetaTileEntityCokeOvenCommon extends RecipeMapPrimitiveMultiblockController {

    @Unique
    private final float pollution$pollutionMultiplier = POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinMetaTileEntityMetaTileEntityCokeOvenCommon(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            if (getOffsetTimer() % 200 == 0 && POConfig.PollutionSystemSwitch.enablePollution) {
                AuraHelper.polluteAura(getWorld(), getPos(), (float) (pollution$pollutionMultiplier * 0.1), POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
            }
        }
    }
}
