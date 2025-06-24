package keqing.pollution.mixin;

import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.recipes.RecipeMap;
import gregtech.common.metatileentities.multi.MetaTileEntityPrimitiveBlastFurnace;
import keqing.pollution.POConfig;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aura.AuraHelper;


@Mixin(MetaTileEntityPrimitiveBlastFurnace.class)
public abstract class MixinMetaTileEntityPrimitiveBlastFurnaceCommon extends RecipeMapPrimitiveMultiblockController {

    @Unique
    private final float pollution$pollutionMultiplier = POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinMetaTileEntityPrimitiveBlastFurnaceCommon(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void injectUpdate(CallbackInfo ci) {
        pollution$pollution();
    }

    @Unique
    private void pollution$pollution() {
        if (!getWorld().isRemote) {
            if (getOffsetTimer() % 200 == 0 && POConfig.PollutionSystemSwitch.enablePollution && recipeMapWorkable.isActive()) {
                AuraHelper.polluteAura(getWorld(), getPos(), (float) (pollution$pollutionMultiplier * 0.1), POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
            }
        }
    }
}
