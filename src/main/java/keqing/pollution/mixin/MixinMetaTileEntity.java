package keqing.pollution.mixin;

import gregtech.api.cover.CoverHolder;
import gregtech.api.metatileentity.IVoidable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.ISyncedTileEntity;
import gregtech.common.ConfigHolder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.aura.AuraHelper;

@Mixin(MetaTileEntity.class)
public abstract class MixinMetaTileEntity implements ISyncedTileEntity, CoverHolder, IVoidable {
	private boolean wasExploded;

	public void doExplosion(float explosionPower) {
		this.setExploded();
		this.getWorld().setBlockToAir(this.getPos());
		this.getWorld().createExplosion((Entity) null, (double) this.getPos().getX() + 0.5, (double) this.getPos().getY() + 0.5, (double) this.getPos().getZ() + 0.5, explosionPower, ConfigHolder.machines.doesExplosionDamagesTerrain);
		AuraHelper.polluteAura(getWorld(), getPos(), 20 * explosionPower, true);
		AuraHelper.drainVis(getWorld(), getPos(), 5 * explosionPower, false);
	}

	protected final void setExploded() {
		this.wasExploded = true;
	}

}
