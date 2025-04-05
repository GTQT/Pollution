package keqing.pollution.mixin;

import gregtech.api.cover.CoverHolder;
import gregtech.api.metatileentity.IVoidable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.ISyncedTileEntity;
import gregtech.common.ConfigHolder;
import keqing.pollution.POConfig;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntity.class)
public abstract class MixinMetaTileEntity implements CoverHolder {

	@Inject(method = "doExplosion", at = @At("HEAD"))
	private void injectDoExplosion(float explosionPower,CallbackInfo ci) {
		if(POConfig.PollutionSystemSwitch.ExplosionPollution) {
			AuraHelper.polluteAura(getWorld(), getPos(), 20 * explosionPower, true);
			AuraHelper.drainVis(getWorld(), getPos(), 5 * explosionPower, false);
		}
	}

}
