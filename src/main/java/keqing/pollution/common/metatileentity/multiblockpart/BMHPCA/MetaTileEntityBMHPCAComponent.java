package keqing.pollution.common.metatileentity.multiblockpart.BMHPCA;


import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IHPCAComponentHatch;
import gregtech.api.capability.IHPCAComputationProvider;
import gregtech.api.capability.IHPCACoolantProvider;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityHPCA;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import java.util.List;

import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static keqing.pollution.common.block.metablocks.POComputerCasing.CasingType.ADVANCED_COMPUTER_CASING;
import static keqing.pollution.common.block.metablocks.POComputerCasing.CasingType.COMPUTER_CASING;

public abstract class MetaTileEntityBMHPCAComponent extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IHPCAComponentHatch>, IHPCAComponentHatch {
    private boolean damaged;

    public MetaTileEntityBMHPCAComponent(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 7);
    }

    public abstract boolean isUltimate();

    public boolean doesAllowBridging() {
        return false;
    }

    public abstract SimpleOverlayRenderer getFrontOverlay();

    public SimpleOverlayRenderer getFrontActiveOverlay() {
        return this.getFrontOverlay();
    }

    protected boolean openGUIOnRightClick() {
        return false;
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    public MultiblockAbility<IHPCAComponentHatch> getAbility() {
        return MultiblockAbility.HPCA_COMPONENT;
    }

    public void registerAbilities(List<IHPCAComponentHatch> abilityList) {
        abilityList.add(this);
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (this.shouldRenderOverlay()) {
            MultiblockControllerBase controller = this.getController();
            SimpleOverlayRenderer renderer;
            if (controller != null && controller.isActive()) {
                renderer = this.getFrontActiveOverlay();
            } else {
                renderer = this.getFrontOverlay();
            }

            if (renderer != null) {
                EnumFacing facing = this.getFrontFacing();
                if (controller instanceof MetaTileEntityHPCA) {
                    facing = RelativeDirection.RIGHT.getRelativeFacing(controller.getFrontFacing(), controller.getUpwardsFacing(), controller.isFlipped());
                }

                renderer.renderSided(facing, renderState, translation, pipeline);
            }
        }

    }

    public ICubeRenderer getBaseTexture() {
        return this.isUltimate() ? POTextures.BMADVANCED_COMPUTER_CASING : POTextures.BMCOMPUTER_CASING;
    }

    public int getDefaultPaintingColor() {
        return 16777215;
    }

    public boolean canPartShare() {
        return false;
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        if (this.isBridge()) {
            tooltip.add(I18n.format("gregtech.machine.hpca.component_type.bridge", new Object[0]));
        }

        int upkeepEUt = this.getUpkeepEUt();
        int maxEUt = this.getMaxEUt();
        if (upkeepEUt != 0 && upkeepEUt != maxEUt) {
            tooltip.add(I18n.format("gregtech.machine.hpca.component_general.upkeep_eut", new Object[]{upkeepEUt}));
        }

        if (maxEUt != 0) {
            tooltip.add(I18n.format("gregtech.machine.hpca.component_general.max_eut", new Object[]{maxEUt}));
        }

        if (this instanceof IHPCACoolantProvider) {
            IHPCACoolantProvider provider = (IHPCACoolantProvider)this;
            if (provider.isActiveCooler()) {
                tooltip.add(I18n.format("gregtech.machine.hpca.component_type.cooler_active", new Object[0]));
                tooltip.add(I18n.format("gregtech.machine.hpca.component_type.cooler_active_coolant", new Object[]{provider.getMaxCoolantPerTick(), I18n.format(Materials.PCBCoolant.getUnlocalizedName(), new Object[0])}));
            } else {
                tooltip.add(I18n.format("gregtech.machine.hpca.component_type.cooler_passive", new Object[0]));
            }

            tooltip.add(I18n.format("gregtech.machine.hpca.component_type.cooler_cooling", new Object[]{provider.getCoolingAmount()}));
        }

        if (this instanceof IHPCAComputationProvider) {
            IHPCAComputationProvider provider = (IHPCAComputationProvider)this;
            tooltip.add(I18n.format("gregtech.machine.hpca.component_type.computation_cwut", new Object[]{provider.getCWUPerTick()}));
            tooltip.add(I18n.format("gregtech.machine.hpca.component_type.computation_cooling", new Object[]{provider.getCoolingPerTick()}));
        }

        if (this.canBeDamaged()) {
            tooltip.add(TooltipHelper.BLINKING_ORANGE + I18n.format("gregtech.machine.hpca.component_type.damaged", new Object[0]));
        }

    }

    public boolean showToolUsages() {
        return false;
    }

    public final boolean isBridge() {
        return this.doesAllowBridging() && (!this.canBeDamaged() || !this.isDamaged());
    }

    public boolean isDamaged() {
        return this.canBeDamaged() && this.damaged;
    }

    public void setDamaged(boolean damaged) {
        if (this.canBeDamaged()) {
            if (this.damaged != damaged) {
                this.damaged = damaged;
                this.markDirty();
                if (this.getWorld() != null && !this.getWorld().isRemote) {
                    this.writeCustomData(GregtechDataCodes.DAMAGE_STATE, (buf) -> {
                        buf.writeBoolean(damaged);
                    });
                }
            }

        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (this.canBeDamaged()) {
            data.setBoolean("Damaged", this.damaged);
        }

        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (this.canBeDamaged()) {
            this.damaged = data.getBoolean("Damaged");
        }

    }

    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        if (this.canBeDamaged()) {
            buf.writeBoolean(this.damaged);
        }

    }

    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        if (this.canBeDamaged()) {
            this.damaged = buf.readBoolean();
        }

    }

    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (this.canBeDamaged() && dataId == GregtechDataCodes.DAMAGE_STATE) {
            this.damaged = buf.readBoolean();
            this.scheduleRenderUpdate();
        }

    }

    public boolean shouldDropWhenDestroyed() {
        return super.shouldDropWhenDestroyed() && (!this.canBeDamaged() || !this.isDamaged());
    }

    public void getDrops(NonNullList<ItemStack> dropsList, EntityPlayer harvester) {
        if (this.canBeDamaged() && this.isDamaged()) {
            if (this.isUltimate()) {
                dropsList.add(PollutionMetaBlocks.COMPUTER_CASING.getItemVariant(ADVANCED_COMPUTER_CASING));
            } else {
                dropsList.add(PollutionMetaBlocks.COMPUTER_CASING.getItemVariant(COMPUTER_CASING));
            }
        }

    }

    public String getMetaName() {
        return this.canBeDamaged() && this.isDamaged() ? super.getMetaName() + ".damaged" : super.getMetaName();
    }
}