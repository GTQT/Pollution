package keqing.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.List;

public class MetaTileEntityManaPoolHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IManaHatch>, IManaHatch{
    int mana=0;
    int MAX_MANA;
    int tier;
    int speed;
    public MetaTileEntityManaPoolHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier=tier;
        this.MAX_MANA= (int) (Math.pow(2,tier)*100000);
        this.speed = (int) (Math.pow(2,tier)*128);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaPoolHatch(metaTileEntityId, getTier());
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.dynamicLabel(7, 30, () -> {
            return "Mana Pool Hatch";
        }, 2302755);
        builder.dynamicLabel(7, 50, () -> {
            return "Tier: " + this.getTier();
        }, 2302755);
        builder.widget((new AdvancedTextWidget(7, 70, this::addDisplayText, 2302755)).setMaxWidthLimit(181));
        return builder.build(this.getHolder(), entityPlayer);
    }
    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentString( "Mana: " + this.mana+" Max: " + this.MAX_MANA));
        textList.add(new TextComponentString( "Speed: " + this.speed));

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
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), this.getController().isActive(), ((IControllable) this.getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, (EnumFacing) null)).isWorkingEnabled());
            } else {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), false, false);
            }
        }

    }

    @Override
    public void update() {
        if(!getWorld().isRemote)
        {
            super.update();
            EnumFacing[] var4 = EnumFacing.VALUES;
            int var5 = var4.length;
            for(int var6 = 0; var6 < var5; ++var6)
            {
                EnumFacing dir = var4[var6];
                if(getWorld().getTileEntity(this.getPos().offset(dir)) instanceof IManaReceiver && !(GTUtility.getMetaTileEntity(getWorld(),this.getPos().offset(dir)) instanceof MetaTileEntityManaPoolHatch))
                {

                    IManaReceiver manaReceiver = (IManaReceiver)getWorld().getTileEntity(this.getPos().offset(dir));
                    int output=Math.min(Math.max(0,this.mana),speed);
                    boolean isf = manaReceiver.isFull();
                    if(GTUtility.getMetaTileEntity(getWorld(),this.getPos().offset(dir)) instanceof MetaTileEntityManaHatch)
                    {
                        MetaTileEntityManaHatch ManaHatch = (MetaTileEntityManaHatch)GTUtility.getMetaTileEntity(getWorld(),this.getPos().offset(dir));
                        isf = ManaHatch.isFull();
                    }
                    if(!isf && output>0)
                    {

                        this.consumeMana(-output);
                        manaReceiver.recieveMana(output);
                    }

                }
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
        return this.MAX_MANA;
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
        if(!this.isFull() || amount<0)
        {
            int amounts = this.getMana() + amount;
            this.mana = Math.max(0, Math.min(amounts, this.MAX_MANA));
            return true;
        }
        return false;
    }


    public boolean isFull() {
        return mana>=MAX_MANA;
    }

    public MultiblockAbility<IManaHatch> getAbility() {
        return POMultiblockAbility.MANA_POOL_HATCH;
    }

    public void registerAbilities(List<IManaHatch> list) {
        list.add(this);
    }

}
