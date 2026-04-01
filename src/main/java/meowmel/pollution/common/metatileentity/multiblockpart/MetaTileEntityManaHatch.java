package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.PipelineUtil;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.IManaReceiver;

import static gregtech.api.GTValues.V;

public class MetaTileEntityManaHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch {

    private final boolean isExport;
    private final int amp;
    private ManaContainer manaContainer;

    public MetaTileEntityManaHatch(ResourceLocation metaTileEntityId, int tier, int amp, boolean isExport) {
        super(metaTileEntityId, tier);
        this.isExport = isExport;
        this.amp = amp;
        manaContainer = new ManaContainer(V[tier] * 64L * amp, amp);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaHatch(this.metaTileEntityId, this.getTier(), amp, isExport);
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(manaContainer);
    }

    @Override
    public void update() {
        super.update();
        if(!isExport)return;
        if (!getWorld().isRemote) {
            for(EnumFacing facing : EnumFacing.VALUES)
            {
                TileEntity tileEntity = getWorld().getTileEntity(getPos().offset(facing));
                if(tileEntity instanceof IManaReceiver manaReceiver)
                {
                    if(!manaReceiver.isFull()){
                        long trans = Math.min(manaContainer.getMana(), V[getTier()] *  amp);
                        manaReceiver.recieveMana((int) trans);
                        manaContainer.removeMana(trans);
                        return;
                    }
                }

                MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(getWorld(), this.getPos().offset(facing));
                if(metaTileEntity instanceof MetaTileEntityManaHatch manaHatch){
                    if(!manaHatch.isExport){
                        if(!manaHatch.isFull()){
                            long trans = Math.min(manaContainer.getMana(), V[getTier()] *  amp);
                            manaHatch.receiveMana((int) trans);
                            manaContainer.removeMana(trans);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public MultiblockAbility<IManaHatch> getAbility() {
        if (isExport) return POMultiblockAbility.MANA_POOL_HATCH;
        return POMultiblockAbility.MANA_HATCH;
    }

    @Override
    public long getMaxMana() {
        return manaContainer.getMaxMana();
    }

    @Override
    public long getMana() {
        return manaContainer.getMana();
    }

    @Override
    public int getAmp() {
        return amp;
    }

    @Override
    public boolean consumeMana(long amount, boolean simulate) {
        return manaContainer.drainMana(amount, simulate);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("store", manaContainer.serializeNBT());
        return super.writeToNBT(data);
    }


    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.manaContainer = new ManaContainer(0, amp);
        this.manaContainer.deserializeNBT(data.getCompoundTag("store"));
    }

    public boolean isFull() {
        return manaContainer.isFull();
    }

    public void receiveMana(int amount) {
        if (!isFull()) manaContainer.addMana(amount);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            getOverlay().renderSided(getFrontFacing(), renderState, translation,
                    PipelineUtil.color(pipeline, GTValues.VC[getTier()]));
        }
    }

    @NotNull
    private SimpleOverlayRenderer getOverlay() {
        if (isExport) {
            if (amp <= 2) {
                return Textures.ENERGY_OUT_MULTI;
            } else if (amp <= 4) {
                return Textures.ENERGY_OUT_HI;
            } else if (amp <= 16) {
                return Textures.ENERGY_OUT_ULTRA;
            } else {
                return Textures.ENERGY_OUT_MAX;
            }
        } else {
            if (amp <= 2) {
                return Textures.ENERGY_IN_MULTI;
            } else if (amp <= 4) {
                return Textures.ENERGY_IN_HI;
            } else if (amp <= 16) {
                return Textures.ENERGY_IN_ULTRA;
            } else {
                return Textures.ENERGY_IN_MAX;
            }
        }
    }
}
