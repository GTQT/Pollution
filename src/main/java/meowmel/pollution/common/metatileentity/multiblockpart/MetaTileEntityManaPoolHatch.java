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
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.utils.PipelineUtil;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import lombok.Getter;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.IManaReceiver;

import static gregtech.api.GTValues.V;

public class MetaTileEntityManaPoolHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch {

    protected final boolean isExport;
    protected ManaContainer manaContainer;

    public MetaTileEntityManaPoolHatch(ResourceLocation metaTileEntityId, int tier, boolean isExport) {
        super(metaTileEntityId, tier);
        this.isExport = isExport;
        manaContainer = new ManaContainer(V[tier] * 64L);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaPoolHatch(this.metaTileEntityId, this.getTier(), isExport);
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
                        long trans = Math.min(manaContainer.getMana(), V[getTier()]);
                        manaReceiver.recieveMana((int) trans);
                        manaContainer.removeMana(trans);
                        return;
                    }
                }

                MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(getWorld(), this.getPos().offset(facing));
                if(metaTileEntity instanceof MetaTileEntityManaPoolHatch manaHatch){
                    if(!manaHatch.isExport){
                        if(!manaHatch.isFull()){
                            long trans = Math.min(manaContainer.getMana(), V[getTier()]);
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
        if (isExport) return POMultiblockAbility.MANA_OUTPUT_POOL;
        return POMultiblockAbility.MANA_INPUT_POOL;
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
    public boolean consumeMana(long amount, boolean simulate) {
        return manaContainer.drainMana(amount, simulate);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("store", manaContainer.serializeNBT());
        return super.writeToNBT(data);
    }


    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.manaContainer = new ManaContainer(0);
        this.manaContainer.deserializeNBT(data.getCompoundTag("store"));
    }

    public boolean isFull() {
        return manaContainer.isFull();
    }

    public void receiveMana(long amount) {
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
    private OrientedOverlayRenderer getOverlay() {
        return Textures.HPCA_OVERLAY;
    }
}
