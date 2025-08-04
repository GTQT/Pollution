package keqing.pollution.mixin.gtsteam;

import gregtech.api.recipes.RecipeMap;
import keqing.gtsteam.api.metatileentity.multiblock.NoEnergyMultiblockController;
import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityIndustrialPrimitiveBlastFurnace;
import keqing.pollution.POConfig;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntityIndustrialPrimitiveBlastFurnace.class)
public abstract class MixinMetaTileEntityIndustrialPrimitiveBlastFurnaceCommon extends NoEnergyMultiblockController {
    @Unique
    private final float pollution$pollutionMultiplier = POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;

    public MixinMetaTileEntityIndustrialPrimitiveBlastFurnaceCommon(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
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
