package keqing.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.Objects;

public class MetaTileEntityManaHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch {
    int mana=0;
    int MAX_MANA;
    int tier;
    public MetaTileEntityManaHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier=tier;
        this.MAX_MANA= (int) (Math.pow(2,tier)*10000);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaHatch(metaTileEntityId, getTier());
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.dynamicLabel(7, 30, () -> "Mana Hatch", 2302755);
        builder.dynamicLabel(7, 50, () -> "Tier: " + this.getTier(), 2302755);
        builder.widget((new AdvancedTextWidget(7, 70, this::addDisplayText, 2302755)).setMaxWidthLimit(181));
        return builder.build(this.getHolder(), entityPlayer);
    }
    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentString( "Mana: " + this.mana+" Max: " + this.MAX_MANA));
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (this.shouldRenderOverlay()) {
            OrientedOverlayRenderer overlayRenderer;
            if (this.getTier() <= 3) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK1_OVERLAY;
            } else if (this.getTier() <= 7) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK2_OVERLAY;
            } else if (this.getTier() <= 10) {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK3_OVERLAY;
            } else {
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK4_OVERLAY;
            }

            if (this.getController() != null && this.getController() instanceof RecipeMapMultiblockController) {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), this.getController().isActive(), Objects.requireNonNull(this.getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null)).isWorkingEnabled());
            } else {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), false, false);
            }
        }

    }

    public boolean canPartShare() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("mana", this.mana);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.mana = data.getInteger("mana");
    }


    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.mana);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.mana = buf.readInt();
    }

    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public boolean consumeMana(int amount) {
        if(mana>amount&&mana-amount>0)
        {
            mana-=amount;
            return true;
        }
        return false;
    }

    public boolean isFull() {
        return mana>=MAX_MANA;
    }

    public void receiveMana(int amount) {

        if(!isFull())mana += amount * getTier();
        this.mana = Math.min(this.mana,this.getMaxMana());
    }

    public MultiblockAbility<IManaHatch> getAbility() {
        return POMultiblockAbility.MANA_HATCH;
    }

    public void registerAbilities(List<IManaHatch> list) {
        list.add(this);
    }
}

