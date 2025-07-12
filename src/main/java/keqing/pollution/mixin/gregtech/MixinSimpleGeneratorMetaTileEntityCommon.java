package keqing.pollution.mixin.gregtech;

import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import keqing.pollution.POConfig;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.aura.AuraHelper;

import java.util.function.Function;

@Mixin(SimpleGeneratorMetaTileEntity.class)
public abstract class MixinSimpleGeneratorMetaTileEntityCommon extends WorkableTieredMetaTileEntity {

    @Unique
    private final float pollution$pollutionMultiplier = (float) (25 + (getTier() - 1) * 15) / 100 * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinSimpleGeneratorMetaTileEntityCommon(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, int tier, Function<Integer, Integer> tankScalingFunction, boolean handlesRecipeOutputs) {
        super(metaTileEntityId, recipeMap, renderer, tier, tankScalingFunction, handlesRecipeOutputs);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            if (getOffsetTimer() % 200 == 0 && POConfig.PollutionSystemSwitch.enablePollution && isActive()) {
                AuraHelper.polluteAura(getWorld(), getPos(), (float) (pollution$pollutionMultiplier * 0.1), POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
            }
        }
    }
}
