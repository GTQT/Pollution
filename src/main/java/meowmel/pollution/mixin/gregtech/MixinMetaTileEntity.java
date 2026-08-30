package meowmel.pollution.mixin.gregtech;

import gregtech.api.cover.CoverHolder;
import gregtech.api.metatileentity.MetaTileEntity;
import meowmel.pollution.POConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntity.class)
public abstract class MixinMetaTileEntity implements CoverHolder {

    @Unique
    int pollution$pollutionTicks = 0;

    /**
     * 公共方法：执行污染和灵气消耗
     * @param amount       污染量（浮点数）
     * @param showEffects  是否显示粒子效果
     */
    @Unique
    private void pollution$applyPollution(float amount, boolean showEffects) {
        AuraHelper.polluteAura(getWorld(), getPos(), amount, showEffects);
        AuraHelper.drainVis(getWorld(), getPos(), amount, false);
    }

    /**
     * @author Meowmel
     * @reason 实现污染
     */
    @Overwrite
    public void pollution(double amount, int ticks) {
        if (POConfig.PollutionSystemSwitch.enablePollution) {
            if (pollution$pollutionTicks >= ticks) {
                amount *= POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;
                // 调用公共方法
                pollution$applyPollution((float) amount, POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
                pollution$pollutionTicks = 0;
            } else {
                pollution$pollutionTicks++;
            }
        }
    }

    @Inject(method = "executeExplosion", at = @At("RETURN"))
    private void pollution$executeExplosion(float explosionPower, CallbackInfo ci) {
        if (POConfig.PollutionSystemSwitch.enableExplosionPollution) {
            float amount = explosionPower * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier;
            // 调用公共方法
            pollution$applyPollution(amount, POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
        }
    }
}