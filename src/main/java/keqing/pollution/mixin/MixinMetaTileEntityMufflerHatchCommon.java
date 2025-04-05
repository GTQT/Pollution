package keqing.pollution.mixin;

import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.POConfig;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntityMufflerHatch.class)
public abstract class MixinMetaTileEntityMufflerHatchCommon extends MetaTileEntityMultiblockPart {

    @Unique
    private final float pollution$pollutionMultiplier = (float) ((100 - (getTier() - 1) * 12.5) / 100 * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier);
    @Shadow
    private boolean frontFaceFree;

    public MixinMetaTileEntityMufflerHatchCommon(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Shadow
    private boolean checkFrontFaceFree() {
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            if (getOffsetTimer() % 10 == 0) this.frontFaceFree = checkFrontFaceFree();
            if (getOffsetTimer() % 200 == 0 && POConfig.PollutionSystemSwitch.enablePollution) {
                AuraHelper.polluteAura(getWorld(), getPos(), (float) (pollution$pollutionMultiplier * 0.1), POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
            }
        }
        if (getWorld().isRemote && getController() instanceof MultiblockWithDisplayBase controller && controller.isActive()) {
            VanillaParticleEffects.mufflerEffect(this, controller.getMufflerParticle());
        }
    }

}
