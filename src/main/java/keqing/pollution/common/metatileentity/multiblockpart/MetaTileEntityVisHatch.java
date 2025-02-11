package keqing.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.api.capability.IVisHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class MetaTileEntityVisHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IVisHatch>, IVisHatch {

    private final int tier;
    private final int visStorageMax;
    private final ItemStackHandler containerInventory;
    private int visStorage;

    public MetaTileEntityVisHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
        this.visStorageMax = 1024 * tier;
        this.containerInventory = new GTItemStackHandler(this, 1);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("ContainerInventory", this.containerInventory.serializeNBT());
        data.setInteger("visStorage", visStorage);
        return super.writeToNBT(data);
    }


    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.containerInventory.deserializeNBT(data.getCompoundTag("ContainerInventory"));
        visStorage = data.getInteger("visStorage");
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        MultiblockControllerBase controller = this.getController();
        if (controller != null) {
            return this.hatchTexture = controller.getBaseTexture(this);
        } else if (this.hatchTexture != null) {
            return this.hatchTexture != Textures.getInactiveTexture(this.hatchTexture) ? (this.hatchTexture = Textures.getInactiveTexture(this.hatchTexture)) : this.hatchTexture;
        } else {
            return POTextures.MAGIC_VOLTAGE_CASINGS[this.tier];
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityVisHatch(this.metaTileEntityId, this.getTier());
    }

    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("等级 %s 容积上限 %s", tier, 1000 * tier));
        tooltip.add(I18n.format("每tick提供 %s 灵气源并消耗 %s 当前区块灵气 ", tier * tier, tier * tier * 0.01));
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void update() {
        super.update();
        if (AuraHelper.drainVis(getWorld(), getPos(), (float) (tier * tier * 0.01), true) >= (float) (tier * tier * 0.01)) {
            if (visStorage < visStorageMax) {
                AuraHelper.drainVis(getWorld(), this.getPos(), (float) (tier * tier * 0.01), false);
                visStorage += tier * tier;
                visStorage=Math.min(visStorageMax,visStorage);
            }
        }
    }


    @Override
    public int getVisStore() {
        return visStorage;
    }

    @Override
    public int getMaxVisStore() {
        return visStorageMax;
    }

    @Override
    public boolean drainVis(int amount, boolean simulate) {
        if (visStorage >= amount) {
            if (!simulate) {
                visStorage -= amount;
            }
            return true;
        }
        return false;
    }


    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 180, 240);
        builder.dynamicLabel(28, 12, () -> "灵气仓", 0xFFFFFF);
        builder.widget(new SlotWidget(containerInventory, 0, 8, 8, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("输入槽位"));
        builder.image(4, 28, 172, 128, GuiTextures.DISPLAY);
        builder.widget((new AdvancedTextWidget(8, 32, this::addDisplayText, 16777215)).setMaxWidthLimit(180));
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 160);
        return builder.build(this.getHolder(), entityPlayer);
    }

    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentTranslation("等级: %s", this.getTier()));
        textList.add(new TextComponentTranslation("灵气源: " + visStorage + "/" + visStorageMax));
    }

    @Override
    public MultiblockAbility<IVisHatch> getAbility() {
        return POMultiblockAbility.VIS_HATCH;
    }

    @Override
    public void registerAbilities(List<IVisHatch> list) {
        list.add(this);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            OrientedOverlayRenderer overlayRenderer;
            if (getTier() <= GTValues.HV)
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK1_OVERLAY;
            else if (getTier() <= GTValues.ZPM)
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK2_OVERLAY;
            else if (getTier() <= GTValues.UEV)
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK3_OVERLAY;
            else
                overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK4_OVERLAY;

            if (getController() != null && getController() instanceof RecipeMapMultiblockController) {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                        getController().isActive(),
                        getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null)
                                .isWorkingEnabled());
            } else {
                overlayRenderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), false, false);
            }
        }
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

}
