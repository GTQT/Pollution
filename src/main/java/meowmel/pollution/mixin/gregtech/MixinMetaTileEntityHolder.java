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
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            manaHatch.receiveMana(mana);
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
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
            return manaHatch.isFull();
        }
        return true;
    }

    @Override
    public int getCurrentMana() {
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            return Math.toIntExact(manaHatch.getMana());
        }
        return 0;
    }

    @Override
    public int getMaxMana() {
        if (metaTileEntity instanceof IManaHatch manaHatch) {
            return Math.toIntExact(manaHatch.getMaxMana());
        }
        return 0;
    }
}