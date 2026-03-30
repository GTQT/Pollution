package meowmel.pollution.mixin.gregtech;

import gregtech.api.cover.CoverHolder;
import gregtech.api.metatileentity.MetaTileEntity;
import meowmel.pollution.POConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntity.class)
public abstract class MixinMetaTileEntity implements CoverHolder {

    int pollutionTicks=0;

    @Overwrite
    public void pollution(double amount, int ticks) {
        if(POConfig.PollutionSystemSwitch.ExplosionPollution) {
            if(pollutionTicks > ticks){
                AuraHelper.polluteAura(getWorld(), getPos(), (float) amount, true);
                AuraHelper.drainVis(getWorld(), getPos(), (float) amount, false);
                pollutionTicks = 0;
            }
        }
    }

}
