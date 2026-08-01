package meowmel.pollution.mixin.gregtech;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.TickableTileEntityBase;
import meowmel.pollution.api.capability.IManaHatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.ManaNetworkEvent;

@Mixin(MetaTileEntityHolder.class)
public abstract class MixinMetaTileEntityHolder extends TickableTileEntityBase implements IManaCollector {
    @Shadow(remap = false)
    MetaTileEntity metaTileEntity;

    @Override
    public void invalidate() {
        ManaNetworkEvent.removeCollector(this);
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        ManaNetworkEvent.removeCollector(this);
        super.onChunkUnload();
    }

    @Override
    public void onLoad() {
        ManaNetworkEvent.addCollector(this);
        super.onLoad();
    }

    @Override
    public void recieveMana(int mana) {
        if (metaTileEntity instanceof IManaHatch manaHatch && manaHatch.canReceiveManaFromBursts()) {
            manaHatch.receiveManaFromBurst(mana);
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return metaTileEntity instanceof IManaHatch manaHatch && manaHatch.canReceiveManaFromBursts();
    }

    @Override
    public void onClientDisplayTick() {

    }

    @Override
    public float getManaYieldMultiplier(IManaBurst burst) {
        return 1;
    }

    @Override
    public boolean isFull() {
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            return manaHatch.isFull() || !manaHatch.canReceiveManaFromBursts();
        }
        return true;
    }

    @Override
    public int getCurrentMana() {
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, manaHatch.getMana()));
        }
        return 0;
    }

    @Override
    public int getMaxMana() {
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, manaHatch.getMaxMana()));
        }
        return 0;
    }
}
