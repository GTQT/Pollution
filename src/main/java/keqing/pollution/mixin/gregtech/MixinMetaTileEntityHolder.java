package keqing.pollution.mixin.gregtech;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.TickableTileEntityBase;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import keqing.pollution.common.metatileentity.single.ManaGeneratorTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.ManaNetworkEvent;

@Mixin(MetaTileEntityHolder.class)
public abstract class MixinMetaTileEntityHolder extends TickableTileEntityBase implements IManaCollector{
    private static final String TAG_MANA = "mana";
    private int mana = 0;

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

    @Shadow(remap = false)
    private MetaTileEntity metaTileEntity;

    public String getName(){
        return "";
    }

    private static final int MAX_MANA = 50000;

    @Override
    public boolean isFull() {
        return mana >= MAX_MANA;
    }
    @Inject(method = "writeToNBT",at = @At("HEAD"))
    public void onWriteToNBT(NBTTagCompound data, CallbackInfoReturnable<NBTTagCompound> compoundCallbackInfoReturnable) {
        data.setInteger(TAG_MANA, mana);
    }

    @Inject(method = "readFromNBT",at = @At("HEAD"))
    public void onReadFromNBT(NBTTagCompound data, CallbackInfo callbackInfo) {
        mana = data.getInteger(TAG_MANA);
    }

    @Override
    public void recieveMana(int mana) {
        if(metaTileEntity instanceof ManaGeneratorTileEntity simpleGeneratorMetaTileEntity) {
            simpleGeneratorMetaTileEntity.reciveMana(mana);
        }
        if(metaTileEntity instanceof MetaTileEntityManaHatch MetaTileEntityMultiblockPart) {
            MetaTileEntityMultiblockPart.receiveMana(mana);
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }
    @Shadow(remap = false)
    @Override
    public void writeInitialSyncData(PacketBuffer buf) {

    }
    @Shadow(remap = false)
    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {

    }
    @Shadow(remap = false)
    @Override
    public void receiveCustomData(int discriminator, PacketBuffer buffer) {

    }
    @Override
    public void onClientDisplayTick() {

    }

    @Override
    public float getManaYieldMultiplier(IManaBurst burst) {
        return 1;
    }

    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }
}